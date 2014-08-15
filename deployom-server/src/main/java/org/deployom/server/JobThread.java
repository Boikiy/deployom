/*
 * The MIT License
 *
 * Copyright (c) 2014 DeployOM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.deployom.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.deployom.core.ConfigService;
import org.deployom.core.HostService;
import org.deployom.core.JobService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Command;
import org.deployom.data.Host;
import org.deployom.data.Job;
import org.deployom.data.Service;
import org.deployom.data.Site;

public class JobThread implements Runnable {

    private static final Logger logger = Logger.getLogger(JobThread.class.getName());
    private final ServletContext context;
    private final String jobName;
    private final String siteName;

    public JobThread(String siteName, String jobName, ServletContext context) {
        this.siteName = siteName;
        this.jobName = jobName;
        this.context = context;
    }

    @Override
    public void run() {

        // Open configuration
        ConfigService configService = new ConfigService();

        // Open Job
        JobService jobService = new JobService(siteName, jobName);

        // Open Site
        Site site = configService.getSite(siteName);

        // If Job or Site update is not enabled
        if (!jobService.isEnabled() || !site.isEnabled()) {
            logger.log(Level.INFO, "Job {0} [{1}] skipped", new Object[]{jobName, siteName});
            return;
        }

        // Started Logging
        logger.log(Level.FINEST, "{0} job started for site {1}", new Object[]{jobName, siteName});

        // Set Running and Remove old Hosts
        jobService.setRunning(true);
        jobService.removeHosts();
        jobService.saveJob();

        try {

            // Discovery
            if ("Discovery".equals(jobName)) {
                runDiscoveryJob(jobService);
            } else {
                runCustomJob(jobService);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.log(Level.WARNING, "Job Thread {0}: {1}", new Object[]{jobName, sw.toString()});
        }

        // Get current time
        Date now = new Date();

        // Set finished
        jobService.setRunning(false);
        jobService.setFinished(now.toString());
        jobService.saveJob();

        // Update Site
        ExecutorService executorSite = (ExecutorService) context.getAttribute("ExecutorSite");
        Future<?> future = executorSite.submit(new SiteThread(siteName, jobName, context));

        // Handle task
        try {
            future.get();
        } catch (ExecutionException ex) {
            logger.log(Level.WARNING, "Site Thread {0}: {1}", new Object[]{siteName, ex});
        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Site Thread {0}: {1}", new Object[]{siteName, ex});
        }

        logger.log(Level.FINEST, "{0} job finished for site {1}", new Object[]{jobName, siteName});
    }

    public void runCustomJob(JobService jobService) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Check Release
        if (siteService.getReleaseName() == null) {
            logger.log(Level.WARNING, "Job {0} skipped, site {1} is updating", new Object[]{jobName, siteName});
            return;
        }

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Job
        Job jobRelease = releaseService.getJob(jobName);
        if (jobRelease == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release {1}", new Object[]{jobName, siteService.getReleaseName()});
            return;
        }

        // Set Schedule
        jobService.setStart(jobRelease.getStart());
        jobService.setPeriod(jobRelease.getPeriod());

        // All hosts in Release
        for (Host hostJob : jobRelease.getHosts()) {

            // All Site hosts
            for (Host host : siteService.getHosts()) {

                Matcher matcher = Pattern.compile(hostJob.getHostType(), Pattern.CASE_INSENSITIVE).matcher(host.getHostType());

                // If not match
                if (matcher == null || !matcher.find()) {
                    continue;
                }

                // Get Host
                Host hostRelease = releaseService.getHost(host.getHostType());
                if (hostRelease == null) {
                    logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                    continue;
                }

                // Create new host for command execution
                HostService hostService = new HostService(host, hostRelease);

                // Add new host to job
                Host jobHost = jobService.addHost(host.getHostName());

                // Get OS Service to check connectivity
                Service os = host.getService("OS");
                if (os == null) {
                    logger.log(Level.WARNING, "Service OS " + " is not found in Release for Host Type {0}", host.getHostType());
                    continue;
                }

                // Run command
                Command hostOnline = hostService.runCommand(os, "Online", jobHost);

                // Check all online service
                for (Service service : host.getServices()) {

                    // If Host Offline
                    if (hostOnline == null || hostOnline.isError()) {

                        // Get existen or create a new Service
                        Service serviceJob = jobHost.getService(service.getServiceName());
                        if (serviceJob == null) {
                            serviceJob = jobHost.addService(service.getServiceName());
                        }
                        serviceJob.setOnline(false);

                        continue;
                    }

                    // Check services for specific checks
                    for (Service serviceJob : hostJob.getServices()) {

                        matcher = Pattern.compile(serviceJob.getServiceName(), Pattern.CASE_INSENSITIVE).matcher(service.getServiceName());

                        // If not match
                        if (matcher == null || !matcher.find()) {
                            continue;
                        }

                        // Execute all Commands as specified in Service
                        for (Command commandJob : serviceJob.getCommands()) {

                            // Skip commands if service offline
                            if (service.isOnline() == false && !"Online".equals(commandJob.getCommandId())) {
                                logger.log(Level.INFO, "Command {0} skipped, service {1} [{2}] is Offline",
                                        new Object[]{commandJob.getCommandId(), service.getServiceName(), host.getHostName()});

                                continue;
                            }

                            // Run command
                            hostService.runCommand(service, commandJob.getCommandId(), jobHost);
                        }
                    }
                }

                // Close sessions
                hostService.closeSessions();

                // Save Job
                jobService.saveJob();
            }
        }
    }

    public void runDiscoveryJob(JobService jobService) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService serviceRelease = new ReleaseService(siteService.getReleaseName());

        // All Site hosts
        for (Host host : siteService.getHosts()) {

            Host hostRelease = serviceRelease.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                continue;
            }

            // Create new host for command execution
            HostService hostService = new HostService(host, hostRelease);

            // Add new host to job
            Host jobHost = jobService.addHost(host.getHostName());

            // Get OS Service to check connectivity
            Service os = host.getService("OS");
            if (os == null) {
                logger.log(Level.WARNING, "Service OS is not found in Release for Host Type {0}", host.getHostType());
                continue;
            }

            // Run Command
            Command hostOnline = hostService.runCommand(os, "Online", jobHost);

            // Check all online service
            for (Service service : hostRelease.getServices()) {

                // Skip assigned Services
                if (host.getService(service.getServiceName()) != null) {
                    continue;
                }

                // Skip If Host Offline
                if (hostOnline == null || hostOnline.isError()) {
                    continue;
                }

                // Run Online Command
                hostService.runCommand(service, "Online", jobHost);
            }

            // Close sessions
            hostService.closeSessions();

            // Save Job
            jobService.saveJob();
        }
    }
}
