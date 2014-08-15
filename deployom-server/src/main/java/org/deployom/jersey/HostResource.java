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
package org.deployom.jersey;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.deployom.core.HistoryService;
import org.deployom.core.HostService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Chart;
import org.deployom.data.Command;
import org.deployom.data.Host;
import org.deployom.data.Service;
import org.deployom.server.SiteThread;
import org.glassfish.jersey.media.sse.SseBroadcaster;

@Path("/Host")
public class HostResource {

    private static final Logger logger = Logger.getLogger(HostResource.class.getName());
    @Context
    ServletContext context;
    private final HistoryService historyService = new HistoryService();

    @POST
    @Path("getChart")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Chart getChart(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Get Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            return null;
        }

        // Find Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Create Host
        HostService hostService = new HostService(host, hostRelease);

        // Check OS exists
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host Type {1}", new Object[]{serviceName, host.getHostType()});
            return null;
        }

        // Get Command
        Command command = serviceRelease.getCommand(commandId);
        if (command == null) {
            logger.log(Level.WARNING, "Command {0} is not found in service {1}", new Object[]{commandId, serviceName});
            return null;
        }

        // Get Chart
        Chart chart = serviceRelease.getChart(commandId);
        if (chart == null) {
            logger.log(Level.WARNING, "Chart {0} is not found in service {1}", new Object[]{commandId, serviceName});
            return null;
        }

        // Get output
        String output = hostService.execCommand(serviceName, commandId);

        // Close sessions
        hostService.closeSessions();

        // If there is no match specified
        if (command.getMatch() == null || "".equals(command.getMatch())) {
            logger.log(Level.WARNING, "Match property is not specified for command {0}", commandId);
            return chart;
        }

        // If command was executed
        if (!"".equals(output)) {

            // Split by lines
            String lines[] = output.split("\r?\n");
            for (String line : lines) {

                Matcher matcher = Pattern.compile(command.getMatch(), Pattern.CASE_INSENSITIVE).matcher(line);

                if (matcher == null || !matcher.find()) {
                    continue;
                }

                // Add tick
                chart.addTick(matcher.group(1));

                // Add lines
                try {
                    if (matcher.group(2) != null) {
                        chart.addLine1(Integer.parseInt(matcher.group(2)));
                    }

                    if (matcher.groupCount() > 2 && matcher.group(3) != null) {
                        chart.addLine2(Integer.parseInt(matcher.group(3)));
                    }

                    if (matcher.groupCount() > 3 && matcher.group(4) != null) {
                        chart.addLine3(Integer.parseInt(matcher.group(4)));
                    }
                } catch (NumberFormatException ex) {
                    logger.log(Level.WARNING, "Graph: parseInt failed {0}", ex);
                }
            }
        }

        return chart;
    }

    @POST
    @Path("runCommand")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Host runCommand(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId,
            @Context HttpServletRequest servletRequest) {

        // Get UserName
        Cookie[] cookies = servletRequest.getCookies();

        String userName = null;
        for (Cookie cookie : cookies) {
            if ("userName".equals(cookie.getName())) {
                userName = cookie.getValue();
            }
        }

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Get Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Site {1}", new Object[]{hostName, siteName});
            return null;
        }

        // Find host based
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Get Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Release''s host {1}",
                    new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Get Command
        Command commandRelease = serviceRelease.getCommand(commandId);
        if (commandRelease == null) {
            logger.log(Level.WARNING, "Command {0} is not found in Release''s service {1}", new Object[]{commandId, serviceRelease.getServiceName()});
            return null;
        }

        // Find Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.INFO, "Service {0} is not found in host {1}, Release Service used", new Object[]{serviceName, host.getHostName()});

            // New Service
            service = new Service();
            service.setServiceName(serviceName);
        }

        // Create Host
        HostService hostService = new HostService(host, hostRelease);

        // Run Command
        Command command = hostService.runCommand(service, commandId, host);

        // Update Site
        ExecutorService executorSite = (ExecutorService) context.getAttribute("ExecutorSite");
        Future<?> future = executorSite.submit(new SiteThread(siteName, host, context));

        // Handle task
        try {
            future.get();
        } catch (ExecutionException ex) {
            logger.log(Level.WARNING, "Site Thread {0}: {1}", new Object[]{siteName, ex});
        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Site Thread {0}: {1}", new Object[]{siteName, ex});
        }

        // Close sessions
        hostService.closeSessions();

        // If operations executed
        if (commandRelease.getGroup() != null && "Operations".equals(commandRelease.getGroup())) {

            // Get Broadcaster
            SseBroadcaster broadcaster = (SseBroadcaster) context.getAttribute("Broadcaster");

            // Set attributes
            command.setSiteName(siteName);
            command.setHostName(hostName);
            command.setServiceName(serviceName);

            // Set User Name
            if (userName != null) {
                command.setUserName(userName);
            }

            // Add Command
            historyService.addCommand(broadcaster, command);
        }

        // Return Host
        return host;
    }
}
