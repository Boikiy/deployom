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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.deployom.core.ConfigService;
import org.deployom.core.ReleaseService;
import org.deployom.data.Host;
import org.deployom.data.Release;
import org.deployom.data.Service;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/Release")
public class ReleaseResource {

    private static final Logger logger = Logger.getLogger(ReleaseResource.class.getName());
    @Context
    ServletContext context;

    @POST
    @Path("addConnection")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addConnection(@FormParam("ReleaseName") String releaseName,
            @FormParam("ConnectionName") String connectionName,
            @FormParam("StartServiceName") String startServiceName,
            @FormParam("StartHostName") String startHostName,
            @FormParam("EndServiceName") String endServiceName,
            @FormParam("EndHostName") String endHostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Connection
        releaseService.addConnection(connectionName, startServiceName, startHostName,
                endServiceName, endHostName);

        return "Added";
    }

    @POST
    @Path("addFlow")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addFlow(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Flow
        releaseService.addFlow(flowName);

        return "Added";
    }

    @POST
    @Path("addFlowHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addFlowHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Flow Host
        releaseService.addFlowHost(flowName, hostName, hostType);

        return "Added";
    }

    @POST
    @Path("addFlowService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addFlowService(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Flow Service
        releaseService.addFlowService(flowName, hostName, serviceName);

        return "Added";
    }

    @POST
    @Path("addHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("Login") String login,
            @FormParam("Password") String password) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Host
        releaseService.addHost(hostType, login, password);

        return "Added";
    }

    @POST
    @Path("addJob")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addJob(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("Start") Integer start,
            @FormParam("Period") Integer period) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Job
        releaseService.addJob(jobName, start, period);

        return "Added";
    }

    @POST
    @Path("addJobCommand")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addJobCommand(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Job Command
        releaseService.addJobCommand(jobName, hostName, serviceName, commandId);

        return "Added";
    }

    @POST
    @Path("addJobHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addJobHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Job Host
        releaseService.addJobHost(jobName, hostName, hostType);

        return "Added";
    }

    @POST
    @Path("addJobService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addJobService(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Job Service
        releaseService.addJobService(jobName, hostName, serviceName);

        return "Added";
    }

    @POST
    @Path("addService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CmdOnline") String cmdOnline,
            @FormParam("CmdOnlineMatch") String cmdOnlineMatch,
            @FormParam("Login") String login,
            @FormParam("Password") String password) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Service
        releaseService.addService(hostType, serviceName, cmdOnline, cmdOnlineMatch, login, password);

        return "Added";
    }

    @POST
    @Path("addServiceChart")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addServiceChart(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ChartId") String chartId,
            @FormParam("Title") String title, @FormParam("Label1") String label1,
            @FormParam("Label2") String label2,
            @FormParam("Label3") String label3) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Service Chart
        releaseService.addServiceChart(hostType, serviceName, chartId, title, label1, label2, label3);

        return "Added";
    }

    @POST
    @Path("addServiceCommand")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addServiceCommand(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId,
            @FormParam("Exec") String exec, @FormParam("Title") String title,
            @FormParam("Group") String group, @FormParam("Match") String match) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Service Command
        releaseService.addServiceCommand(hostType, serviceName, commandId, exec, title, group, match);

        return "Added";
    }

    @POST
    @Path("addServiceLink")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addServiceLink(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ServiceLink") String serviceLink,
            @FormParam("Login") String login,
            @FormParam("Password") String password) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Service Link
        releaseService.addServiceLink(hostType, serviceName, serviceLink, login, password);

        return "Added";
    }

    @POST
    @Path("addServiceModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addServiceModule(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName,
            @FormParam("Context") String context,
            @FormParam("Login") String login,
            @FormParam("Password") String password,
            @FormParam("Port") Integer port) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Service Module
        releaseService.addServiceModule(hostType, serviceName, moduleName, context, login, password, port);

        return "Added";
    }

    @POST
    @Path("addSiteHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addSiteHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Site Host
        releaseService.addSiteHost(hostName, hostType);

        return "Added";
    }

    @POST
    @Path("addSiteService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addSiteService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("HostFilter") String hostFilter) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Add Site Service
        releaseService.addSiteService(hostName, serviceName, hostFilter);

        return "Added";
    }

    @POST
    @Path("copyService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String copyService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("NewReleaseName") String newReleaseName,
            @FormParam("NewHostType") String newHostType,
            @FormParam("NewServiceName") String newServiceName) {

        // Check data
        if ("".equals(serviceName) || "".equals(newServiceName) || "".equals(newHostType)) {
            return "Required data is missing";
        }

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Get Service
        Service service = releaseService.getService(hostType, serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostType});
            return "Release data is wrong";
        }

        // New Service
        Service newService = new Service(service);
        newServiceName = newServiceName.replaceAll(ConfigService.PATTERN, "");
        newService.setServiceName(newServiceName.toUpperCase());

        // Copy in the same Template
        if (releaseName.equals(newReleaseName)) {
            releaseService.addService(newHostType, newService);
            return "Copied";
        }

        // Open Release
        ReleaseService newReleaseService = new ReleaseService(newReleaseName);

        // Copy Service
        newReleaseService.addService(newHostType, newService);
        return "Copied";
    }

    @POST
    @Path("downloadRelease")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response downloadRelease(@FormParam("ReleaseName") String releaseName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Open File
        File file = new File(releaseService.getFileName());

        // Create Response
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=" + releaseService.getReleaseName() + ".release.json");

        // Return Response
        return response.build();
    }

    @POST
    @Path("getConnections")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Release getConnections(@FormParam("ReleaseName") String releaseName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove other data
        releaseService.getFlows().clear();
        releaseService.getJobs().clear();
        releaseService.getHosts().clear();

        // Return Connections
        return releaseService.getRelease();
    }

    @POST
    @Path("getHost")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Host getHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Return Host
        return releaseService.getRelease().getHostByType(hostType);
    }

    @POST
    @Path("getHosts")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Release getHosts(@FormParam("ReleaseName") String releaseName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Services in Hosts
        for (Host host : releaseService.getHosts()) {
            host.getServices().clear();
        }

        // Remove other data
        releaseService.getFlows().clear();
        releaseService.getJobs().clear();
        releaseService.getConnections().clear();

        // Return Hosts with Removed Services
        return releaseService.getRelease();
    }

    @POST
    @Path("getRelease")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Release getRelease(@FormParam("ReleaseName") String releaseName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Return Release
        return releaseService.getRelease();
    }

    @POST
    @Path("removeConnection")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeConnection(@FormParam("ReleaseName") String releaseName,
            @FormParam("ConnectionName") String connectionName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Connection
        releaseService.removeConnection(connectionName);

        return "Removed";
    }

    @POST
    @Path("removeFlow")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeFlow(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Flow
        releaseService.removeFlow(flowName);

        return "Removed";
    }

    @POST
    @Path("removeFlowHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeFlowHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Flow Host
        releaseService.removeFlowHost(flowName, hostName);

        return "Removed";
    }

    @POST
    @Path("removeFlowService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeFlowService(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Flow Service
        releaseService.removeFlowService(flowName, hostName, serviceName);

        return "Removed";
    }

    @POST
    @Path("removeHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Host
        releaseService.removeHost(hostType);

        return "Removed";
    }

    @POST
    @Path("removeJob")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeJob(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Job
        releaseService.removeJob(jobName);

        return "Removed";
    }

    @POST
    @Path("removeJobCommand")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeJobCommand(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Job Command
        releaseService.removeJobCommand(jobName, hostName, serviceName, commandId);

        return "Removed";
    }

    @POST
    @Path("removeJobHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeJobHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove job Host
        releaseService.removeJobHost(jobName, hostName);

        return "Removed";
    }

    @POST
    @Path("removeJobService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeJobService(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Job Service
        releaseService.removeJobService(jobName, hostName, serviceName);

        return "Removed";
    }

    @POST
    @Path("removeService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Service
        releaseService.removeService(hostType, serviceName);

        return "Removed";
    }

    @POST
    @Path("removeServiceChart")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeServiceChart(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ChartId") String chartId) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Service Chart
        releaseService.removeServiceChart(hostType, serviceName, chartId);

        return "Removed";
    }

    @POST
    @Path("removeServiceCommand")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeServiceCommand(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Service Command
        releaseService.removeServiceCommand(hostType, serviceName, commandId);

        return "Removed";
    }

    @POST
    @Path("removeServiceModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeServiceModule(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Service Module
        releaseService.removeServiceModule(hostType, serviceName, moduleName);

        return "Removed";
    }

    @POST
    @Path("removeSiteHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeSiteHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Site Host
        releaseService.removeSiteHost(hostName);

        return "Removed";
    }

    @POST
    @Path("removeSiteService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeSiteService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Remove Site Service
        releaseService.removeSiteService(hostName, serviceName);

        return "Removed";
    }

    @POST
    @Path("renameFlow")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String renameFlow(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("NewFlowName") String newFlowName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Rename Flow
        releaseService.renameFlow(flowName, newFlowName);

        return "Renamed";
    }

    @POST
    @Path("renameFlowHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String renameFlowHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName,
            @FormParam("NewHostName") String newHostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Rename Flow Host
        releaseService.renameFlowHost(flowName, hostName, newHostName);

        return "Renamed";
    }

    @POST
    @Path("renameHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String renameHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("NewHostType") String newHostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Rename Host
        releaseService.renameHost(hostType, newHostType);

        return "Renamed";
    }

    @POST
    @Path("renameService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String renameService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("NewServiceName") String newServiceName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Rename Service
        releaseService.renameService(hostType, serviceName, newServiceName);

        return "Renamed";
    }

    @POST
    @Path("replaceServiceCommands")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String replaceServiceCommands(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("Match") String match,
            @FormParam("Replace") String replace) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Replace Service Commands
        releaseService.replaceServiceCommands(hostType, serviceName, match, replace);

        return "Replaced";
    }

    @POST
    @Path("transferService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String transferService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("NewHostType") String newHostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Transfer Service
        releaseService.transferService(hostType, serviceName, newHostType);

        return "Transfered";
    }

    @POST
    @Path("updateCommandPassword")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateCommandPassword(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("Login") String login,
            @FormParam("OldPassword") String oldPassword,
            @FormParam("NewPassword") String newPassword) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Command Password
        releaseService.updateCommandPassword(login, oldPassword, newPassword);

        return "Updated";
    }

    @POST
    @Path("updateConnection")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateConnection(@FormParam("ReleaseName") String releaseName,
            @FormParam("ConnectionName") String connectionName,
            @FormParam("StartServiceName") String startServiceName,
            @FormParam("StartHostName") String startHostName,
            @FormParam("EndServiceName") String endServiceName,
            @FormParam("EndHostName") String endHostName) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Connection
        releaseService.updateConnection(connectionName, startServiceName, startHostName,
                endServiceName, endHostName);

        return "Updated";
    }

    @POST
    @Path("updateFlow")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateFlow(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("Filter") String filter) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Flow
        releaseService.updateFlow(flowName, filter);

        return "Updated";
    }

    @POST
    @Path("updateFlowHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateFlowHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("FlowName") String flowName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Flow Host
        releaseService.updateFlowHost(flowName, hostName, hostType);

        return "Updated";
    }

    @POST
    @Path("updateJob")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateJob(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("Start") Integer start,
            @FormParam("Period") Integer period) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Job
        releaseService.updateJob(jobName, start, period);

        return "Changed";
    }

    @POST
    @Path("updateJobHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateJobHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Job Host
        releaseService.updateJobHost(jobName, hostName, hostType);

        return "Updated";
    }

    @POST
    @Path("updateService")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateService(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ServiceLink") String serviceLink,
            @FormParam("Login") String login,
            @FormParam("Password") String password) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Service
        releaseService.updateService(hostType, serviceName, serviceLink, login, password);

        return "Updated";
    }

    @POST
    @Path("updateServiceChart")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateServiceChart(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ChartId") String chartId,
            @FormParam("Title") String title, @FormParam("Label1") String label1,
            @FormParam("Label2") String label2,
            @FormParam("Label3") String label3) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Service Chart
        releaseService.updateServiceChart(hostType, serviceName, chartId, title, label1, label2, label3);

        return "Updated";
    }

    @POST
    @Path("updateServiceCommand")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateServiceCommand(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("CommandId") String commandId,
            @FormParam("Exec") String exec, @FormParam("Title") String title,
            @FormParam("Group") String group, @FormParam("Match") String match,
            @FormParam("Match2") String match2,
            @FormParam("NotMatch") String notMatch,
            @FormParam("NotMatch2") String notMatch2) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Service Command
        releaseService.updateServiceCommand(hostType, serviceName, commandId, exec, title, group, match, match2, notMatch, notMatch2);

        return "Updated";
    }

    @POST
    @Path("updateServiceModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateServiceModule(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostType") String hostType,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName,
            @FormParam("Context") String context,
            @FormParam("Login") String login,
            @FormParam("Password") String password,
            @FormParam("Port") Integer port) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Service Module
        releaseService.updateServiceModule(hostType, serviceName, moduleName, context, login, password, port);

        return "Updated";
    }

    @POST
    @Path("updateServicePassword")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateServicePassword(
            @FormParam("ReleaseName") String releaseName,
            @FormParam("Login") String login,
            @FormParam("OldPassword") String oldPassword,
            @FormParam("NewPassword") String newPassword) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Service Password
        releaseService.updateServicePassword(login, oldPassword, newPassword);

        return "Updated";
    }

    @POST
    @Path("updateSiteHost")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateSiteHost(@FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType) {

        // Open Release
        ReleaseService releaseService = new ReleaseService(releaseName);

        // Update Site Host
        releaseService.updateSiteHost(hostName, hostType);

        return "Updated";
    }

    @POST
    @Path("uploadRelease")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadRelease(@FormDataParam("ReleaseName") String releaseName,
            @FormDataParam("Release") InputStream releaseStream,
            @FormDataParam("Release") FormDataContentDisposition releaseDetail,
            @Context HttpServletResponse servletResponse) {

        // Upload Release
        if (releaseStream != null) {
            ReleaseService releaseService = new ReleaseService(releaseName);
            releaseService.uploadStream(releaseName, releaseStream, releaseDetail.getFileName());
        }

        // Redirect
        try {
            servletResponse.sendRedirect("/designer");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Release {0}: Can''t construct URL for redirection: {1}", new Object[]{releaseName, ex});
        }

        return "Uploaded";
    }
}
