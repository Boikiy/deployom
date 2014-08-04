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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.deployom.core.ConfigService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Job;
import org.deployom.data.Site;
import org.glassfish.jersey.media.sse.SseBroadcaster;

@WebListener
public class Listener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(Listener.class.getName());

    private SseBroadcaster broadcaster;
    private ExecutorService jobThread;
    private List<ScheduledExecutorService> schedulers;
    private ExecutorService siteThread;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        // Shutdown schedulers
        for (ScheduledExecutorService scheduler : schedulers) {
            scheduler.shutdownNow();
        }

        // Shutdown Executors
        jobThread.shutdownNow();
        siteThread.shutdownNow();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // Get context
        ServletContext context = sce.getServletContext();

        // Create configuration if required
        ConfigService configService = new ConfigService();

        // Create Broadcaster for SSE
        broadcaster = new SseBroadcaster();
        context.setAttribute("Broadcaster", broadcaster);

        // Create Executor for Manual Job Running
        jobThread = Executors.newSingleThreadExecutor();
        context.setAttribute("JobThread", jobThread);

        // Create Executor for Update Site
        siteThread = Executors.newSingleThreadExecutor();
        context.setAttribute("SiteThread", siteThread);

        // Initialize schedulers
        schedulers = new ArrayList<ScheduledExecutorService>();

        // Check all sites
        for (Site site : configService.getSites()) {

            // Skip remote Sites
            if (site.getServerURL() != null) {
                continue;
            }

            // Open Site
            SiteService siteService = new SiteService(site.getSiteName());

            // Open Release
            ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

            // Check all jobs
            for (Job jobRelease : releaseService.getJobs()) {

                // Get start for Job
                Integer start = jobRelease.getStart();

                // Get period for Job
                Integer period = jobRelease.getPeriod();

                // Skip manual job
                if (start == 0 || period == 0) {
                    continue;
                }

                // Create Scheduler
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                schedulers.add(scheduler);

                // Logging
                logger.log(Level.INFO, "Job {0} [{1}] scheduled to start in {2} minutes and period is {3} minutes",
                        new Object[]{jobRelease.getJobName(), site.getSiteName(), start, period});

                // Schedule Job
                scheduler.scheduleAtFixedRate(new JobThread(site.getSiteName(), jobRelease.getJobName(), context),
                        start, period, TimeUnit.MINUTES);
            }
        }
    }
}
