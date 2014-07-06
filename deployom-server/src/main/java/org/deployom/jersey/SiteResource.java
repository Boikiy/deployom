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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.deployom.core.ConfigService;
import org.deployom.core.HostService;
import org.deployom.core.JobService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Chart;
import org.deployom.data.Command;
import org.deployom.data.Event;
import org.deployom.data.Flow;
import org.deployom.data.Host;
import org.deployom.data.Job;
import org.deployom.data.Module;
import org.deployom.data.Service;
import org.deployom.data.Site;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/Site")
public class SiteResource {

    private static final Logger logger = Logger.getLogger(SiteResource.class.getName());
    @Context
    ServletContext context;

    @POST
    @Path("addHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addHost(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType,
            @FormParam("IP") String IP) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Add Host
        if (siteService.addHost(hostName, hostType, IP) == null) {
            return "Host " + hostName + " [" + siteName + "] failed to add";
        }

        // Return
        return siteName + ": Host " + hostName + " added";
    }

    @POST
    @Path("addService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addService(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Add Service
        siteService.addService(hostName, serviceName);

        return "Added";
    }

    @POST
    @Path("downloadSite")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response downloadSite(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open File
        File file = new File(siteService.getFileName());

        // Create Response
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=" + siteService.getSiteName() + ".site.json");

        // Return Response
        return response.build();
    }

    @POST
    @Path("getCharts")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getCharts(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Check all hosts
        for (Host host : siteService.getHosts()) {

            // Looking for Host Type
            Host hostRelease = releaseService.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                return null;
            }

            // Check all services
            for (Service service : host.getServices()) {

                // Get Service
                Service serviceRelease = hostRelease.getService(service.getServiceName());
                if (serviceRelease == null) {
                    logger.log(Level.WARNING, "Service {0} is not found in Host Type {1} in Release",
                            new Object[]{service.getServiceName(), host.getHostType()});
                    continue;
                }

                // Get Charts
                for (Chart chartRelease : serviceRelease.getCharts()) {

                    // Add Chart
                    service.addChart(chartRelease);
                }
            }
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getEvents")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getEvents(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Check all hosts
        for (Host host : siteService.getHosts()) {

            // Looking for Host Type
            Host hostRelease = releaseService.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                return null;
            }

            // Check all events
            for (Event event : host.getEvents()) {

                // Get Service
                Service serviceRelease = hostRelease.getService(event.getServiceName());
                if (serviceRelease == null) {
                    logger.log(Level.WARNING, "Service {0} is not found in Release for Host Type {1}",
                            new Object[]{event.getServiceName(), host.getHostType()});
                    continue;
                }

                // Get Command
                Command commandRelease = serviceRelease.getCommand(event.getCommandId());
                if (commandRelease == null) {
                    logger.log(Level.WARNING, "Command {0} is not found in Service {1}",
                            new Object[]{event.getCommandId(), serviceRelease.getServiceName()});
                    continue;
                }

                // Set Event Exec and Title
                event.setTitle(commandRelease.getTitle());
                event.setExec(commandRelease.getExec());
            }

            // Remove Services
            host.getServices().clear();
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getFlow")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Flow getFlow(@FormParam("SiteName") String siteName,
            @FormParam("FlowName") String flowName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Get Flow
        Flow flow = releaseService.getFlow(flowName);

        // Create a new Flow
        Flow siteFlow = new Flow();
        siteFlow.setFlowName(flow.getFlowName());
        siteFlow.setFilter(flow.getFilter());

        // For all Flow Hosts
        for (Host flowHost : flow.getHosts()) {

            // Create new Host
            Host siteHost = new Host();
            siteHost.setHostName(flowHost.getHostName());
            siteFlow.addHost(siteHost);

            // Check all Site Hosts
            for (Host host : siteService.getHosts()) {

                // Check if Host Type match the Flow
                Matcher matcher = Pattern.compile(flowHost.getHostType(), Pattern.CASE_INSENSITIVE).matcher(host.getHostType());
                if (matcher == null || !matcher.find()) {
                    continue;
                }

                // Looking for Host Type
                Host hostRelease = releaseService.getHost(host.getHostType());
                if (hostRelease == null) {
                    logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                    return null;
                }

                // Check all flow Hosts Services
                for (Service serviceFlow : flowHost.getServices()) {

                    // Check all site Hosts Services
                    for (Service service : host.getServices()) {

                        // Check if service name matches the Flow
                        matcher = Pattern.compile(serviceFlow.getServiceName(), Pattern.CASE_INSENSITIVE).matcher(service.getServiceName());
                        if (matcher == null || !matcher.find()) {
                            continue;
                        }

                        // Set hostName and IP into Service
                        service.setHostName(host.getHostName());
                        service.setIP(host.getIP());

                        // Get Service
                        Service serviceRelease = hostRelease.getService(service.getServiceName());
                        if (serviceRelease == null) {
                            logger.log(Level.WARNING, "Service {0} is not found in Host Type {1} in Release",
                                    new Object[]{service.getServiceName(), host.getHostType()});
                            continue;
                        }

                        // Get a Commands
                        for (Command commandRelease : serviceRelease.getCommands()) {

                            // New command
                            Command command = new Command();
                            command.setCommandId(commandRelease.getCommandId());
                            command.setTitle(commandRelease.getTitle());
                            command.setGroup(commandRelease.getGroup());
                            command.setExec(commandRelease.getExec());

                            // Check Event
                            Event event = host.getEvent(service.getServiceName(), commandRelease.getCommandId());
                            if (event != null) {
                                command.setError(true);
                            }

                            // Add command
                            service.addCommand(command);
                        }

                        // Get a Charts
                        for (Chart chartRelease : serviceRelease.getCharts()) {

                            // Add chart
                            service.addChart(chartRelease);
                        }

                        // Add Service into new Site Host
                        siteHost.addService(service);
                    }
                }
            }

            // Sort services
            Collections.sort(siteHost.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.toString().compareToIgnoreCase(s2.toString());
                }
            });
        }

        // Return Flow
        return siteFlow;
    }

    @POST
    @Path("getFlows")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getFlows(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Config
        ConfigService configService = new ConfigService();

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Create Site
        Site site = new Site();
        site.setSiteName(siteName);
        site.setReleaseName(siteService.getReleaseName());

        for (Flow flow : releaseService.getFlows()) {

            // Create a new Flow
            Flow siteFlow = new Flow();
            siteFlow.setFlowName(flow.getFlowName());

            // For all Flow Hosts
            for (Host hostFlow : flow.getHosts()) {

                // Create new Host
                Host siteHost = new Host();
                siteHost.setHostName(hostFlow.getHostName());
                siteFlow.addHost(siteHost);

                // Check all Site Hosts
                for (Host host : siteService.getHosts()) {

                    // Check if Host Type match the Flow
                    Matcher matcher = Pattern.compile(hostFlow.getHostType(), Pattern.CASE_INSENSITIVE).matcher(host.getHostType());
                    if (matcher == null || !matcher.find()) {
                        continue;
                    }

                    // Looking for Host Type
                    Host hostRelease = releaseService.getHost(host.getHostType());
                    if (hostRelease == null) {
                        logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                        return null;
                    }

                    // Check all flow Hosts Services
                    for (Service serviceFlow : hostFlow.getServices()) {

                        // Check all site Hosts Services
                        for (Service service : host.getServices()) {

                            // Set hostName into Service
                            service.setHostName(host.getHostName());

                            // Check if service name matches the Flow
                            matcher = Pattern.compile(serviceFlow.getServiceName(), Pattern.CASE_INSENSITIVE).matcher(service.getServiceName());
                            if (matcher == null || !matcher.find()) {
                                continue;
                            }

                            // Add Service into new Site Host
                            siteHost.addService(service);

                            // Get Service
                            Service serviceRelease = hostRelease.getService(service.getServiceName());
                            if (serviceRelease == null) {
                                logger.log(Level.WARNING, "Service {0} is not found in Host Type {1} in Release",
                                        new Object[]{service.getServiceName(), host.getHostType()});
                                continue;
                            }

                            // Check Modules
                            for (Module moduleRelease : serviceRelease.getModules()) {

                                // Get Config Modules
                                Module moduleConfig = configService.getModule(moduleRelease.getModuleName());
                                if (moduleConfig == null) {
                                    logger.log(Level.WARNING, "Module {0} is not found in Config", moduleRelease.getModuleName());
                                    continue;
                                }

                                // Add module
                                site.addModule(moduleConfig);
                            }
                        }
                    }
                }
            }

            // Add Flow
            site.addFlow(siteFlow);
        }

        // Return Site
        return site;
    }

    @POST
    @Path("getHosts")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getHosts(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("Exec") String exec,
            @FormParam("HostPattern") String hostPattern,
            @FormParam("TypePattern") String typePattern) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release if not opened
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Create host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Check OS exists
        Service service = host.getService("OS");
        if (service == null) {
            logger.log(Level.WARNING, "Can''t find OS service in Host {0}", host.getHostName());
            return null;
        }

        // Get Host from Release
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Check OS Service exists
        Service serviceRelease = hostRelease.getService("OS");
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Can''t find OS service in {0} in Release", host.getHostType());
            return null;
        }

        // Create Host
        HostService hostService = new HostService(host, hostRelease);

        // Create Hosts Command
        Command hostsCommand = new Command();
        hostsCommand.setCommandId("GetHosts");
        hostsCommand.setExec(exec);
        serviceRelease.addCommand(hostsCommand);

        // Execute hosts command inside OS service
        String hostsOutput = hostService.execCommand("OS", "GetHosts");
        hostService.closeSessions();

        // Split by lines
        String lines[] = hostsOutput.split("\r?\n");
        for (String line : lines) {

            // Looking for IP and HostName
            Matcher matcherHost = Pattern.compile(hostPattern, Pattern.CASE_INSENSITIVE).matcher(line);
            if (!matcherHost.find() || "".equals(matcherHost.group(1)) || "localhost".equals(matcherHost.group(1))) {
                continue;
            }

            // Host already added
            if (siteService.getHost(matcherHost.group(2)) != null) {
                continue;
            }

            // Create host based
            Host newHost = new Host();
            newHost.setHostName(matcherHost.group(2));
            newHost.setIP(matcherHost.group(1));
            newHost.setInfo(line);

            // Looking for Type
            Matcher matcherType = Pattern.compile(typePattern, Pattern.CASE_INSENSITIVE).matcher(line);
            if (matcherType.find()) {
                newHost.setHostType(matcherType.group(1).toUpperCase());
            }

            // Add Host
            siteService.getSite().addHost(newHost);
        }

        // Sort hosts
        Collections.sort(siteService.getSite().getHosts(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                return h1.toString().compareToIgnoreCase(h2.toString());
            }
        });

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getJobs")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getJobs(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Remove Hosts
        siteService.getHosts().clear();

        for (Job job : releaseService.getJobs()) {
            JobService jobService = new JobService(siteName, job.getJobName());

            // Remove Hosts
            jobService.getHosts().clear();

            // Add Job
            siteService.getSite().addJob(jobService.getJob());
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getLayout")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getLayout(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Check all hosts
        for (Host host : siteService.getHosts()) {

            // Looking for Host Type
            Host hostRelease = releaseService.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                return null;
            }

            // Add new services
            for (Service serviceRelease : hostRelease.getServices()) {

                // Get Service
                Service service = host.getService(serviceRelease.getServiceName());

                // If service not exists
                if (service == null) {

                    // Add Service
                    service = host.addService(serviceRelease.getServiceName());

                    // Service wasn't added
                    if (service == null) {
                        continue;
                    }

                    // Set Online as null to identify new Services
                    service.setOnline(null);
                }

                // Add Online Command
                for (Command commandRelease : serviceRelease.getCommands()) {

                    // Skip non-Online commands
                    if (!"Online".equals(commandRelease.getCommandId())) {
                        continue;
                    }

                    // New command
                    Command command = new Command();
                    command.setTitle(commandRelease.getTitle());
                    command.setCommandId(commandRelease.getCommandId());
                    command.setExec(commandRelease.getExec());

                    // Add Command
                    service.addCommand(command);
                }
            }

            // Sort services
            Collections.sort(host.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.getServiceName().compareToIgnoreCase(s2.getServiceName());
                }
            });
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getMap")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getMap(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Get Site
        Site siteRelease = releaseService.getSite();

        // Create a Site Map
        Site siteMap = new Site();
        siteMap.setSiteName(siteService.getSiteName());
        siteMap.setReleaseName(siteService.getReleaseName());

        // For all site Hosts
        for (Host hostRelease : siteRelease.getHosts()) {

            // Create new Host
            Host newHost = new Host();
            newHost.setHostName(hostRelease.getHostName());
            siteMap.addHost(newHost);

            // Check all Site Hosts
            for (Host host : siteService.getHosts()) {

                // Check if Host Type match the Site
                Matcher matcher = Pattern.compile(hostRelease.getHostType(), Pattern.CASE_INSENSITIVE).matcher(host.getHostType());
                if (matcher == null || !matcher.find()) {
                    continue;
                }

                // Check all flow Hosts Services
                for (Service serviceRelease : hostRelease.getServices()) {

                    // Check all site Hosts Services
                    for (Service service : host.getServices()) {

                        // Check if service name matches the Site configuration
                        matcher = Pattern.compile(serviceRelease.getServiceName(), Pattern.CASE_INSENSITIVE).matcher(service.getServiceName());
                        if (matcher == null || !matcher.find()) {
                            continue;
                        }

                        // Check if hostname specified
                        if (serviceRelease.getHostName() != null) {

                            // Check if host name matches the Site configuration
                            matcher = Pattern.compile(serviceRelease.getHostName(), Pattern.CASE_INSENSITIVE).matcher(host.getHostName());
                            if (matcher == null || !matcher.find()) {
                                continue;
                            }
                        }

                        // Set hostName and IP into Service
                        service.setHostName(host.getHostName());
                        service.setIP(host.getIP());

                        // Add Service into new Site Host
                        newHost.addService(service);
                    }
                }
            }

            // Sort services
            Collections.sort(newHost.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.getHostName().compareToIgnoreCase(s2.getHostName());
                }
            });
        }

        // Return Site
        return siteMap;
    }

    @POST
    @Path("getModules")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getModules(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Check all hosts
        for (Host host : siteService.getHosts()) {

            // Looking for Host Type
            Host hostRelease = releaseService.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                return null;
            }

            // Check all services
            for (Service service : host.getServices()) {

                // Get Service from Release
                Service serviceRelease = hostRelease.getService(service.getServiceName());
                if (serviceRelease == null) {
                    logger.log(Level.WARNING, "Service {0} is not found in Host Type {1} in Release",
                            new Object[]{service.getServiceName(), host.getHostType()});
                    continue;
                }

                // Get modules
                for (Module moduleRelease : serviceRelease.getModules()) {

                    // Add Module
                    service.addModule(moduleRelease);
                }
            }
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getServices")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getServices(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Check all hosts
        for (Host host : siteService.getHosts()) {

            // Looking for Host Type
            Host hostRelease = releaseService.getHost(host.getHostType());
            if (hostRelease == null) {
                logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
                return null;
            }

            // Check all services
            for (Service service : host.getServices()) {

                // Get Service from Release
                Service serviceRelease = hostRelease.getService(service.getServiceName());
                if (serviceRelease == null) {
                    logger.log(Level.WARNING, "Service {0} is not found in Host Type {1} in Release",
                            new Object[]{service.getServiceName(), host.getHostType()});
                    continue;
                }

                // Get a menus for commands
                for (Command commandRelease : serviceRelease.getCommands()) {

                    // New command
                    Command command = new Command();
                    command.setCommandId(commandRelease.getCommandId());
                    command.setTitle(commandRelease.getTitle());
                    command.setGroup(commandRelease.getGroup());
                    command.setExec(commandRelease.getExec());

                    // Check Event
                    Event event = host.getEvent(service.getServiceName(), commandRelease.getCommandId());
                    if (event != null) {
                        command.setError(true);
                    }

                    // Add Commands for menu
                    service.addCommand(command);
                }

                // Get Charts
                for (Chart chartRelease : serviceRelease.getCharts()) {
                    service.addChart(chartRelease);
                }

                // Get Modules
                for (Module moduleRelease : serviceRelease.getModules()) {
                    service.addModule(moduleRelease);
                }
            }
        }

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("getSite")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getSite(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Return Site
        return siteService.getSite();
    }

    @POST
    @Path("removeEvents")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeEvents(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Remove Events
        siteService.removeEvents();

        return "Removed";
    }

    @POST
    @Path("removeHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeHost(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Remove Host
        if (siteService.removeHost(hostName) == null) {
            return "Host " + hostName + " [" + siteName + "] failed to remove";
        }

        return siteName + ": Host " + hostName + " removed";
    }

    @POST
    @Path("removeService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeService(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Remove Service
        siteService.removeService(hostName, serviceName);

        return "Removed";
    }

    @POST
    @Path("renameHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String renameHost(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("NewHostName") String newHostName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Rename Host
        siteService.renameHost(hostName, newHostName);

        return "Renamed";
    }

    @POST
    @Path("updateHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateHost(@FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType,
            @FormParam("IP") String IP) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Update Host
        siteService.updateHost(hostName, hostType, IP);

        return "Updated";
    }

    @POST
    @Path("uploadSite")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadSite(@FormDataParam("SiteName") String siteName,
            @FormDataParam("Site") InputStream siteStream,
            @FormDataParam("Site") FormDataContentDisposition siteDetail,
            @Context HttpServletResponse servletResponse) {

        // Upload Site
        if (siteStream != null) {
            SiteService siteService = new SiteService(siteName);
            siteService.uploadStream(siteName, siteStream, siteDetail.getFileName());
        }

        // Redirect
        try {
            servletResponse.sendRedirect("/site");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Site {0}: Can''t construct URL for redirection: {1}", new Object[]{siteName, ex});
        }

        return Response.serverError().build();
    }
}
