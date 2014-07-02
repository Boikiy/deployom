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
package org.deployom.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.deployom.core.HistoryService;
import org.deployom.core.JobService;
import org.deployom.core.MailService;
import org.deployom.core.SiteService;
import org.deployom.data.Command;
import org.deployom.data.Event;
import org.deployom.data.Host;
import org.deployom.data.Service;
import org.glassfish.jersey.media.sse.SseBroadcaster;

public class SiteThread implements Runnable {

    private static final Logger logger = Logger.getLogger(SiteThread.class.getName());
    private final SseBroadcaster broadcaster;
    HistoryService historyService = new HistoryService();
    private final String jobName;
    private final String siteName;
    private final Host updatedHost;

    public SiteThread(String siteName, String jobName, ServletContext context) {

        this.siteName = siteName;
        this.jobName = jobName;
        this.broadcaster = (SseBroadcaster) context.getAttribute("Broadcaster");
        updatedHost = null;
    }

    public SiteThread(String siteName, Host host, ServletContext context) {

        this.siteName = siteName;
        this.updatedHost = host;
        this.jobName = null;
        this.broadcaster = (SseBroadcaster) context.getAttribute("Broadcaster");
    }

    @Override
    public void run() {

        try {

            // Update Site based on Job
            if (jobName != null) {
                updateSiteJob();
                return;
            }

            // Update Site based on Host
            if (updatedHost != null) {
                updateSiteHost();
                return;
            }

            logger.log(Level.WARNING, "Site Thread {0}: No updates found", siteName);

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.log(Level.WARNING, "Site Thread {0}: {1}", new Object[]{siteName, sw.toString()});
        }
    }

    public void updateSiteHost() {

        // Started Logging
        logger.log(Level.FINEST, "Updating Site {0} started for host {1}",
                new Object[]{siteName, updatedHost.getHostName()});

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Get Host for update
        Host host = siteService.getHost(updatedHost.getHostName());
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in site {1}", new Object[]{updatedHost.getHostName(), siteName});
            return;
        }

        // Update all online services
        for (Service updatedService : updatedHost.getServices()) {

            // Get Service in Host
            Service service = host.getService(updatedService.getServiceName());

            // No Service to update
            if (service == null) {
                continue;
            }

            // Update Online & Error states
            service.setOnline(updatedService.isOnline());

            // Update events
            for (Command jobCommand : updatedService.getCommands()) {

                // Get Event
                Event event = host.getEvent(service.getServiceName(), jobCommand.getCommandId());

                // If event not found and error happened
                if (event == null && jobCommand.isError() == true) {
                    event = host.addEvent("OnDemand", service.getServiceName(), jobCommand.getCommandId());

                    // Update Event History
                    event.setSiteName(siteName);
                    event.setHostName(host.getHostName());
                    event.setExec(jobCommand.getExec());
                    event.setTitle(jobCommand.getTitle());
                    event.setOut(jobCommand.getOut());
                    historyService.addEvent(broadcaster, event);

                    // Continue
                    continue;
                }

                // If event exists and error disappeared
                if (event != null && jobCommand.isError() == false) {
                    host.removeEvent(service.getServiceName(), jobCommand.getCommandId());
                }
            }
        }

        // Save Site
        siteService.saveSite();

        // Finished Logging
        logger.log(Level.FINEST, "Updating Site {0} finished for host {1}",
                new Object[]{siteName, host.getHostName()});
    }

    public void updateSiteJob() {

        // Started Logging
        logger.log(Level.FINEST, "Updating Site {0} started for job {1}",
                new Object[]{siteName, jobName});

        // Open Job
        JobService jobService = new JobService(siteName, jobName);

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Get Mail
        MailService mailService = new MailService();

        // Update all processed hosts
        for (Host jobHost : jobService.getHosts()) {

            // Get Host for update
            Host host = siteService.getHost(jobHost.getHostName());

            // If Host not found
            if (host == null) {
                continue;
            }

            // Update all online services
            for (Service updatedService : jobHost.getServices()) {

                // Get Service in Host
                Service service = host.getService(updatedService.getServiceName());

                // If service not found and Online
                if (service == null && updatedService.isOnline() == true) {

                    // Add Service if Online
                    service = host.addService(updatedService.getServiceName());

                    // Broadcast
                    historyService.broadcastMessage(broadcaster, "Service " + service.getServiceName() + " on " + host.getHostName() + " is Enabled");
                }

                // No Service to update
                if (service == null) {
                    continue;
                }

                // Update Online & Error states
                service.setOnline(updatedService.isOnline());

                // Update events
                for (Command jobCommand : updatedService.getCommands()) {

                    // Get Event
                    Event event = host.getEvent(service.getServiceName(), jobCommand.getCommandId());

                    // If event not found and error happened
                    if (event == null && jobCommand.isError() == true) {
                        event = host.addEvent(jobName, service.getServiceName(), jobCommand.getCommandId());

                        // Update Event History
                        event.setSiteName(siteName);
                        event.setHostName(host.getHostName());
                        event.setExec(jobCommand.getExec());
                        event.setTitle(jobCommand.getTitle());
                        event.setOut(jobCommand.getOut());
                        historyService.addEvent(broadcaster, event);

                        // Send mail
                        String subject = siteName + ": " + event.toString();
                        String text = event.getTitle() + "\n\n=> " + event.getExec() + "\n\n" + event.getOut();
                        mailService.sendMail(subject, text);

                        // Continue
                        continue;
                    }

                    // If event exists and error disappeared
                    if (event != null && jobCommand.isError() == false) {
                        host.removeEvent(service.getServiceName(), jobCommand.getCommandId());
                    }
                }
            }
        }

        // Save Site
        siteService.saveSite();

        // Finished Logging
        logger.log(Level.FINEST, "Updating Site {0} finished for job {1}", new Object[]{siteName, jobName});
    }
}
