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
package org.deployom.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Chart;
import org.deployom.data.Command;
import org.deployom.data.Connection;
import org.deployom.data.Flow;
import org.deployom.data.Host;
import org.deployom.data.Job;
import org.deployom.data.Module;
import org.deployom.data.Release;
import org.deployom.data.Service;
import org.deployom.data.Site;
import org.deployom.server.Start;

public class ReleaseService {

    private static final Logger logger = Logger.getLogger(ReleaseService.class.getName());
    private Release release;

    public ReleaseService(String releaseName) {

        // Create new Release
        release = new Release();
        release.setReleaseName(releaseName);

        // Open Release
        openRelease();
    }

    public Boolean addConnection(String connectionName, String startServiceName,
            String startHostName, String endServiceName, String endHostName) {

        // Check Connection Name
        if ("".equals(connectionName)) {
            return false;
        }

        // Keep only required symbols
        connectionName = connectionName.replaceAll(ConfigService.PATTERN, "");

        // Create a Start service entity...
        Service start = new Service();
        start.setServiceName(startServiceName);
        start.setHostName(startHostName);

        // Create an End service entity...
        Service end = new Service();
        end.setServiceName(endServiceName);
        end.setHostName(endHostName);

        // Create a Flow entity...
        Connection connection = new Connection();
        connection.setConnectionName(connectionName);
        connection.setEnd(end);
        connection.setStart(start);

        // Add connection
        release.addConnection(connection);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addFlow(String flowName) {

        // Check Flow Name
        if ("".equals(flowName)) {
            return false;
        }

        // Keep only required symbols
        flowName = flowName.replaceAll(ConfigService.PATTERN, "");

        // Create a Flow entity...
        Flow flow = new Flow();
        flow.setFlowName(flowName);

        // Add flow
        release.addFlow(flow);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addFlowHost(String flowName, String hostName, String hostType) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Create a Host entity...
        Host host = new Host();
        host.setHostName(hostName);
        host.setHostType(hostType);

        // Add Host into flow
        flow.addHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addFlowService(String flowName, String hostName,
            String serviceName) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Looking for a Host entity...
        Host host = flow.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Flow {1}", new Object[]{hostName, flowName});
            return false;
        }

        // Create new Service, because it can contain regex
        Service service = new Service();
        service.setServiceName(serviceName);

        // Add service into Host
        host.addService(service);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Host addHost(String hostType, String login, String password) {

        // Create new Host
        Host host = new Host();
        host.setHostType(hostType.toUpperCase());
        host.setHostName(hostType.toUpperCase());

        // Add OS Service
        Service service = host.addService("OS");
        service.setLogin(login);
        service.setPassword(ConfigService.encryptBlowfish(password));

        // Create Online command
        Command command = new Command();
        command.setCommandId("Online");
        command.setExec("hostname");
        command.setTitle("Online");
        command.setMatch(hostType);
        service.addCommand(command);

        // Add Host
        release.addHost(host);

        // Save Release
        saveRelease();

        // Return new Host
        return host;
    }

    public Boolean addJob(String jobName, Integer start, Integer period) {

        // Check Job Name
        if ("".equals(jobName)) {
            return false;
        }

        // Keep only required symbols
        jobName = jobName.replaceAll(ConfigService.PATTERN, "");

        // Create a Flow entity...
        Job job = new Job();
        job.setJobName(jobName);
        job.setStart(start);
        job.setPeriod(period);

        // Add Job
        release.addJob(job);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addJobCommand(String jobName, String hostName,
            String serviceName, String commandId) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Job {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Looking for a Service entity...
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Job {1}", new Object[]{serviceName, jobName});
            return false;
        }

        // Keep only required symbols
        commandId = commandId.replaceAll(ConfigService.PATTERN, "");

        // Create new Command
        Command command = new Command();
        command.setCommandId(commandId);

        // Add command into Service
        service.addCommand(command);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addJobHost(String jobName, String hostName, String hostType) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Create a Host entity...
        Host host = new Host();
        host.setHostName(hostName);
        host.setHostType(hostType);

        // Add Host into job
        job.addHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addJobService(String jobName, String hostName,
            String serviceName) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Job {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Create new Service, because it can contain regex
        Service service = new Service();
        service.setServiceName(serviceName);

        // Add service into Host
        host.addService(service);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Service addService(String hostType, String serviceName, String exec,
            String match, String login, String password) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Keep only required symbols
        serviceName = serviceName.replaceAll(ConfigService.PATTERN, "");

        // Create a new Service
        Service service;

        // Add Default properties to service
        service = host.addService(serviceName);
        service.setLogin(login);
        service.setPassword(ConfigService.encryptBlowfish(password));

        // Create command
        Command command = new Command();
        command.setCommandId("Online");
        command.setTitle("Online");
        command.setExec(exec);

        // Add Match
        if (!"".equals(match)) {
            command.setMatch(match);
        }

        // Add command
        service.addCommand(command);

        // Save Release
        saveRelease();

        // Return service
        return service;
    }

    public Service addService(String hostType, Service service) {

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service newService = host.getService(service.getServiceName());

        // Check New Service
        if (newService != null) {
            logger.log(Level.WARNING, "Service {0} is already defined in Host {1}", new Object[]{service.getServiceName(), hostType});
            return null;
        }

        // Copy Service
        newService = new Service(service);

        // Add New Service
        host.addService(newService);

        // Save Release
        saveRelease();

        // Return updated Service
        return newService;
    }

    public Service addServiceChart(String hostType, String serviceName,
            String chartId, String title, String label1, String label2,
            String label3) {

        // Check Service Name and Chart
        if ("".equals(serviceName) || "".equals(chartId)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Create command
        Chart chart = new Chart();
        chart.setChartId(chartId);
        chart.setTitle(title);

        // Set Labels
        if (!"".equals(label1)) {
            chart.setLabel1(label1);
        }

        if (!"".equals(label2)) {
            chart.setLabel2(label2);
        }

        if (!"".equals(label3)) {
            chart.setLabel3(label3);
        }

        // Add Chart
        service.addChart(chart);

        // Save Release
        saveRelease();

        // Return service
        return service;
    }

    public Service addServiceCommand(String hostType, String serviceName,
            String commandId, String exec, String title, String group,
            String match) {

        // Check Service Name and Command
        if ("".equals(serviceName) || "".equals(commandId) || "".equals(exec)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Create command
        Command command = new Command();
        command.setCommandId(commandId);
        command.setExec(exec);

        // Set Match
        if (!"".equals(match)) {
            command.setMatch(match);
        }

        // Set Title
        if (!"".equals(title)) {
            command.setTitle(title);
        }

        // Set Group
        if (!"".equals(group)) {
            command.setGroup(group);
        }

        // Add Command
        service.addCommand(command);

        // Save Release
        saveRelease();

        // Return updated service
        return service;
    }

    public Service addServiceLink(String hostType, String serviceName,
            String serviceLink, String login, String password) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Host
        Host linkedHost = release.getHostByType(serviceLink);
        if (linkedHost == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", serviceLink);
            return null;
        }

        // Open Service
        Service linkedService = linkedHost.getService(serviceName);
        if (linkedService == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, linkedHost.getHostName()});
            return null;
        }

        // Keep only required symbols
        serviceName = serviceName.replaceAll(ConfigService.PATTERN, "");

        // Add Link to service
        Service service = host.addService(serviceName);
        service.setServiceLink(serviceLink);
        service.setLogin(linkedService.getLogin());
        service.setPassword(linkedService.getPassword());

        // Save Release
        saveRelease();

        // Return service
        return service;
    }

    public Service addServiceModule(String hostType, String serviceName,
            String moduleName, String context, String login, String password,
            Integer port) {

        // Check Service Name and Module
        if ("".equals(serviceName) || "".equals(moduleName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Create module
        Module module = new Module();
        module.setModuleName(moduleName);

        // Set Context
        if (!"".equals(context)) {
            module.setContext(context);
        }

        // Set Login
        if (!"".equals(login)) {
            module.setLogin(login);
        }

        // Encrypt password
        String encrypted = ConfigService.encryptBlowfish(password);

        // If password encrypted
        if (encrypted != null) {
            module.setPassword(encrypted);
        }

        // Set Port
        if (port != null) {
            module.setPort(port);
        }

        // Add Module
        service.addModule(module);

        // Save Release
        saveRelease();

        // Return updated service
        return service;
    }

    public Boolean addSiteHost(String hostName, String hostType) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Site entity...
        Site site = release.getSite();
        if (site == null) {
            logger.warning("Site configuration is not found in Release");
            return false;
        }

        // Create a Host entity...
        Host host = new Host();
        host.setHostName(hostName);
        host.setHostType(hostType);

        // Add Host into Site
        site.addHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean addSiteService(String hostName, String serviceName,
            String hostFilter) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Site entity...
        Site site = release.getSite();
        if (site == null) {
            logger.warning("Site configuration is not found in Release");
            return false;
        }

        // Looking for a Host entity...
        Host host = site.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Group {0} is not found in Site Release", hostName);
            return false;
        }

        // Create new Service, because it can contain regex
        Service service = new Service();
        service.setServiceName(serviceName);
        service.setHostName(hostFilter);

        // Add service into Host
        host.addService(service);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public List<Connection> getConnections() {

        return release.getConnections();
    }

    public String getFileName() {

        return Start.DATA_DIR + release.getReleaseName() + ".release.json";
    }

    public Flow getFlow(String flowName) {

        return release.getFlow(flowName);
    }

    public List<Flow> getFlows() {

        return release.getFlows();
    }

    public Host getHost(String hostType) {

        // Get Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            return null;
        }

        // Update linked services
        for (Service service : host.getServices()) {
            if (service.getServiceLink() != null && !"".equals(service.getServiceLink())) {

                // Get Linked Host
                Host hostLink = release.getHostByType(service.getServiceLink());
                if (hostLink == null) {
                    logger.log(Level.WARNING, "Can''t find linked Host Type {0} for service {1}",
                            new Object[]{service.getServiceLink(), service.getServiceName()});
                    continue;
                }

                // Get Linked service
                Service serviceLink = hostLink.getService(service.getServiceName());
                if (serviceLink == null) {
                    logger.log(Level.WARNING, "Can''t find linked service in Host Type {0} for service {1}",
                            new Object[]{service.getServiceLink(), service.getServiceName()});
                    continue;
                }

                // Add all non-existen commands
                for (Command command : serviceLink.getCommands()) {
                    if (service.getCommand(command.getCommandId()) != null) {
                        continue;
                    }

                    // Add Command
                    service.addCommand(command);
                }

                // Add all non-existen chart
                for (Chart chart : serviceLink.getCharts()) {
                    if (service.getChart(chart.getChartId()) != null) {
                        continue;
                    }

                    // Add Chart
                    service.addChart(chart);
                }

                // Add all non-existen modules
                for (Module module : serviceLink.getModules()) {
                    if (service.getModule(module.getModuleName()) != null) {
                        continue;
                    }

                    // Add Module
                    service.addModule(module);
                }
            }
        }

        return host;
    }

    public List<Host> getHosts() {

        return release.getHosts();
    }

    public Job getJob(String jobName) {

        return release.getJob(jobName);
    }

    public List<Job> getJobs() {

        return release.getJobs();
    }

    public Release getRelease() {

        return release;
    }

    public String getReleaseName() {

        return release.getReleaseName();
    }

    public Service getService(String hostType, String serviceName) {

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Return Service
        return host.getService(serviceName);
    }

    public Site getSite() {

        return release.getSite();
    }

    public final Release openRelease() {

        // Read JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            release = mapper.readValue(new File(getFileName()), Release.class);

            // Return
            return release;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        }

        return null;
    }

    public Boolean removeConnection(String connectionName) {

        // Check Connection
        if ("".equals(connectionName)) {
            return false;
        }

        // Looking for a Flow entity...
        Connection connection = release.getConnection(connectionName);
        if (connection == null) {
            logger.log(Level.WARNING, "Connection {0} is not found in Release", connectionName);
            return false;
        }

        // Remove Connnection
        release.removeConnection(connection);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeFlow(String flowName) {

        // Check Flow
        if ("".equals(flowName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Remove flow
        release.removeFlow(flow);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeFlowHost(String flowName, String hostName) {

        // Check Host
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Looking for a Host entity...
        Host host = flow.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Flow {1}", new Object[]{hostName, flowName});
            return false;
        }

        // Remove Host into flow
        flow.removeHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeFlowService(String flowName, String hostName,
            String serviceName) {

        // Check Service
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Looking for a Host entity...
        Host host = flow.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Flow {1}", new Object[]{hostName, flowName});
            return false;
        }

        // Remove service from Host
        host.removeService(serviceName);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeHost(String hostType) {

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return false;
        }

        // Remove Host
        release.removeHost(host);

        // Save Release
        saveRelease();

        // Return
        return true;
    }

    public Boolean removeJob(String jobName) {

        // Check Job Name
        if ("".equals(jobName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Remove job
        release.removeJob(job);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeJobCommand(String jobName, String hostName,
            String serviceName, String commandId) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Job {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Looking for a Service entity...
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Job {1}", new Object[]{serviceName, jobName});
            return false;
        }

        // Looking for a Command entity...
        Command command = service.getCommand(commandId);
        if (command == null) {
            logger.log(Level.WARNING, "Command {0} is not found in Service {1}", new Object[]{commandId, serviceName});
            return false;
        }

        // Remove command from Service
        service.removeCommand(command);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeJobHost(String jobName, String hostName) {

        // Check Host
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Flow {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Remove Host into Job
        job.removeHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeJobService(String jobName, String hostName,
            String serviceName) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Job {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Remove service from Host
        host.removeService(serviceName);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeService(String hostType, String serviceName) {

        // Create new Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return false;
        }

        // Remove service
        host.removeService(serviceName);

        // Save Release
        saveRelease();

        // Return updated Host
        return true;
    }

    public Boolean removeServiceChart(String hostType, String serviceName,
            String chartId) {

        // Check Service and Chart
        if ("".equals(serviceName) || "".equals(chartId)) {
            return false;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return false;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return false;
        }

        // Open Chart
        Chart chart = service.getChart(chartId);
        if (chart == null) {
            logger.log(Level.WARNING, "Chart {0} is not found in Service {1}", new Object[]{chartId, service.getServiceName()});
            return false;
        }

        // Remove Chart
        service.removeChart(chart);

        // Save Release
        saveRelease();

        // Return updated Service
        return true;
    }

    public Service removeServiceCommand(String hostType, String serviceName,
            String commandId) {

        // Check Service and Command
        if ("".equals(serviceName) || "".equals(commandId)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Command
        Command command = service.getCommand(commandId);
        if (command == null) {
            logger.log(Level.WARNING, "Command {0} is not found in Service {1}", new Object[]{commandId, service.getServiceName()});
            return null;
        }

        // Remove Command
        service.removeCommand(command);

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Service removeServiceModule(String hostType, String serviceName,
            String moduleName) {

        // Check Service Name and Module
        if ("".equals(serviceName) || "".equals(moduleName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Module
        Module module = service.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, service.getServiceName()});
            return null;
        }

        // Remove Module
        service.removeModule(module);

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Boolean removeSiteHost(String hostName) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Site entity...
        Site site = release.getSite();
        if (site == null) {
            logger.warning("Site configuration is not found in Release");
            return false;
        }

        // Looking for a Host entity...
        Host host = site.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Site", hostName);
            return false;
        }

        // Remove Host from Site
        site.removeHost(host);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean removeSiteService(String hostName, String serviceName) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return false;
        }

        // Looking for a Site entity...
        Site site = release.getSite();
        if (site == null) {
            logger.warning("Site configuration is not found in Release");
            return false;
        }

        // Looking for a Host entity...
        Host host = site.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Site", hostName);
            return false;
        }

        // Remove service from Host
        host.removeService(serviceName);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean renameFlow(String flowName, String newFlowName) {

        // Check Flow Name
        if ("".equals(flowName)) {
            return false;
        }

        // Create a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "{0} flow is not found in Release", flowName);
            return false;
        }

        // Rename Flow
        flow.setFlowName(newFlowName);

        // Update configuration
        saveRelease();

        // Return true
        return true;
    }

    public Boolean renameFlowHost(String flowName, String hostName,
            String newHostName) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Looking for a Host entity...
        Host host = flow.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in flow {1}", new Object[]{hostName, flowName});
            return false;
        }

        // Rename Host
        host.setHostName(newHostName);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Host renameHost(String hostType, String newHostType) {

        // Check Host Type
        if ("".equals(newHostType)) {
            return null;
        }

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Rename Host
        host.setHostName(newHostType.toUpperCase());
        host.setHostType(newHostType.toUpperCase());

        // Save Release
        saveRelease();

        // Return updated Host
        return host;
    }

    public Service renameService(String hostType, String serviceName,
            String newServiceName) {

        // Check Service Names
        if ("".equals(serviceName) || "".equals(newServiceName)) {
            return null;
        }

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Service
        Service newService = host.getService(newServiceName);

        // Check New Service
        if (newService != null) {
            logger.log(Level.WARNING, "Service {0} is already defined in Host {1}", new Object[]{newServiceName, host.getHostName()});
            return null;
        }

        // Rename Service
        service.setServiceName(newServiceName);

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Service replaceServiceCommands(String hostType, String serviceName,
            String match, String replace) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Compile regex
        Pattern pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);

        for (Command command : service.getCommands()) {

            // Get Exec
            String exec = command.getExec();

            // If exec not found
            if (exec == null) {
                continue;
            }

            // Get Matcher
            Matcher matcher = pattern.matcher(exec);

            // If matcher not found
            if (matcher == null) {
                continue;
            }

            // Replace
            command.setExec(pattern.matcher(exec).replaceAll(replace));
        }

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Release saveRelease() {

        // Sort flows
        Collections.sort(release.getFlows(), new Comparator<Flow>() {
            @Override
            public int compare(Flow f1, Flow f2) {
                return f1.toString().compareToIgnoreCase(f2.toString());
            }
        });

        // Sort connections
        Collections.sort(release.getConnections(), new Comparator<Connection>() {
            @Override
            public int compare(Connection c1, Connection c2) {
                return c1.toString().compareToIgnoreCase(c2.toString());
            }
        });

        // Sort jobs
        Collections.sort(release.getJobs(), new Comparator<Job>() {
            @Override
            public int compare(Job j1, Job j2) {
                return j1.toString().compareToIgnoreCase(j2.toString());
            }
        });

        // Sort hosts
        Collections.sort(release.getSite().getHosts(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                return h1.toString().compareToIgnoreCase(h2.toString());
            }
        });

        // Sort All Hosts in Flows
        for (Flow flow : release.getFlows()) {

            // Sort hosts
            Collections.sort(flow.getHosts(), new Comparator<Host>() {
                @Override
                public int compare(Host h1, Host h2) {
                    return h1.toString().compareToIgnoreCase(h2.toString());
                }
            });

            for (Host host : flow.getHosts()) {
                // Sort Services
                Collections.sort(host.getServices(), new Comparator<Service>() {
                    @Override
                    public int compare(Service s1, Service s2) {
                        return s1.toString().compareToIgnoreCase(s2.toString());
                    }
                });
            }
        }

        for (Host host : release.getSite().getHosts()) {
            // Sort Services
            Collections.sort(host.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.toString().compareToIgnoreCase(s2.toString());
                }
            });
        }

        // Sort All Hosts in Jobs
        for (Job job : release.getJobs()) {

            // Sort hosts
            Collections.sort(job.getHosts(), new Comparator<Host>() {
                @Override
                public int compare(Host h1, Host h2) {
                    return h1.toString().compareToIgnoreCase(h2.toString());
                }
            });

            for (Host host : job.getHosts()) {
                // Sort Services
                Collections.sort(host.getServices(), new Comparator<Service>() {
                    @Override
                    public int compare(Service s1, Service s2) {
                        return s1.toString().compareToIgnoreCase(s2.toString());
                    }
                });

                for (Service service : host.getServices()) {
                    // Sort Commands
                    Collections.sort(service.getCommands(), new Comparator<Command>() {
                        @Override
                        public int compare(Command c1, Command c2) {
                            return c1.toString().compareToIgnoreCase(c2.toString());
                        }
                    });
                }
            }
        }

        // Sort hosts
        Collections.sort(release.getHosts(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                return h1.toString().compareToIgnoreCase(h2.toString());
            }
        });

        // Check Hosts
        for (Host host : release.getHosts()) {

            // Sort Services
            Collections.sort(host.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.toString().compareToIgnoreCase(s2.toString());
                }
            });

            for (Service service : host.getServices()) {
                // Sort Commands
                Collections.sort(service.getCommands(), new Comparator<Command>() {
                    @Override
                    public int compare(Command c1, Command c2) {
                        return c1.toString().compareToIgnoreCase(c2.toString());
                    }
                });

                // Sort Charts
                Collections.sort(service.getCharts(), new Comparator<Chart>() {
                    @Override
                    public int compare(Chart c1, Chart c2) {
                        return c1.toString().compareToIgnoreCase(c2.toString());
                    }
                });

                // Sort Modules
                Collections.sort(service.getModules(), new Comparator<Module>() {
                    @Override
                    public int compare(Module m1, Module m2) {
                        return m1.toString().compareToIgnoreCase(m2.toString());
                    }
                });
            }
        }

        // Update version
        release.setVersion(release.getVersion() + 1);

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), release);
            return release;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
        }

        return null;
    }

    public Service transferService(String hostType, String serviceName,
            String newHostType) {

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Looking for Host Type
        Host newHost = release.getHostByType(newHostType);
        if (newHost == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", newHostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Add Service to new hostType
        if (newHost.addService(service) != null) {

            // Remove Service from old hostType
            host.removeService(serviceName);
        }

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Boolean updateCommandPassword(String login, String oldPassword,
            String newPassword) {

        // Compile regex
        Pattern pattern = Pattern.compile(login + "/" + oldPassword, Pattern.CASE_INSENSITIVE);

        // Check all service
        for (Host host : release.getHosts()) {

            for (Service service : host.getServices()) {

                for (Command command : service.getCommands()) {

                    // Get Exec
                    String exec = command.getExec();

                    // If exec not found
                    if (exec == null) {
                        continue;
                    }

                    // Get Matcher
                    Matcher matcher = pattern.matcher(exec);

                    // If matcher not found
                    if (matcher == null) {
                        continue;
                    }

                    // Replace
                    command.setExec(pattern.matcher(exec).replaceAll(login + "/" + newPassword));
                }
            }
        }

        // Save Release
        saveRelease();

        // Return
        return true;
    }

    public Boolean updateConnection(String connectionName,
            String startServiceName, String startHostName, String endServiceName,
            String endHostName) {

        // Check Connection Name
        if ("".equals(connectionName)) {
            return false;
        }

        // Create a Connection entity...
        Connection connection = release.getConnection(connectionName);
        if (connection == null) {
            logger.log(Level.WARNING, "{0} connection is not found in Release", connectionName);
            return false;
        }

        // Create a Start service entity...
        Service start = connection.getStart();
        start.setServiceName(startServiceName);
        start.setHostName(startHostName);

        // Create an End service entity...
        Service end = connection.getEnd();
        end.setServiceName(endServiceName);
        end.setHostName(endHostName);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean updateFlow(String flowName, String filter) {

        // Check Flow
        if ("".equals(flowName)) {
            return false;
        }

        // Create a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "{0} flow is not found in Release", flowName);
            return false;
        }

        // Update Filter
        flow.setFilter(filter);

        // Update configuration
        saveRelease();

        // Return true
        return true;
    }

    public Boolean updateFlowHost(String flowName, String hostName,
            String hostType) {

        // Check Host
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Flow entity...
        Flow flow = release.getFlow(flowName);
        if (flow == null) {
            logger.log(Level.WARNING, "Flow {0} is not found in Release", flowName);
            return false;
        }

        // Looking for a Host entity...
        Host host = flow.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in flow {1}", new Object[]{hostName, flowName});
            return false;
        }

        // Update Host
        host.setHostType(hostType);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Boolean updateJob(String jobName, Integer start, Integer period) {

        // Check Job
        if ("".equals(jobName)) {
            return false;
        }

        // Create a Flow entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "{0} job is not found in Release", jobName);
            return false;
        }

        // Update Start and Period
        job.setStart(start);
        job.setPeriod(period);

        // Update configuration
        saveRelease();

        // Return true
        return true;
    }

    public Boolean updateJobHost(String jobName, String hostName,
            String hostType) {

        // Check Host
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Job entity...
        Job job = release.getJob(jobName);
        if (job == null) {
            logger.log(Level.WARNING, "Job {0} is not found in Release", jobName);
            return false;
        }

        // Looking for a Host entity...
        Host host = job.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in job {1}", new Object[]{hostName, jobName});
            return false;
        }

        // Update Host
        host.setHostType(hostType);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public Service updateService(String hostType, String serviceName,
            String serviceLink, String login, String password) {

        // Check Service
        if ("".equals(serviceName)) {
            return null;
        }

        // Looking for Host Type
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);

        // Check Service
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Update Linked Service
        if (!"".equals(serviceLink)) {
            service.setServiceLink(serviceLink);
        }

        // Update Service
        service.setLogin(login);
        service.setPassword(ConfigService.encryptBlowfish(password));

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Service updateServiceChart(String hostType, String serviceName,
            String chartId, String title, String label1, String label2,
            String label3) {

        // Check Service and Chart
        if ("".equals(serviceName) || "".equals(chartId)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Command
        Chart chart = service.getChart(chartId);
        if (chart == null) {
            logger.log(Level.WARNING, "Chart {0} is not found in Service {1}", new Object[]{chartId, service.getServiceName()});
            return null;
        }

        // Update Command
        chart.setTitle(title);

        // Update Labels
        if (!"".equals(label1)) {
            chart.setLabel1(label1);
        }

        if (!"".equals(label2)) {
            chart.setLabel2(label2);
        }

        if (!"".equals(label3)) {
            chart.setLabel3(label3);
        }

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Service updateServiceCommand(String hostType, String serviceName,
            String commandId, String exec, String title, String group,
            String match, String match2, String notMatch, String notMatch2) {

        // Check Service and Command
        if ("".equals(serviceName) || "".equals(commandId)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Command
        Command command = service.getCommand(commandId);
        if (command == null) {
            logger.log(Level.WARNING, "Command {0} is not found in Service {1}", new Object[]{commandId, service.getServiceName()});
            return null;
        }

        // Update Command
        command.setExec(exec);

        // Update Title
        if (!"".equals(title)) {
            command.setTitle(title);
        }

        // Update Group
        if (!"".equals(group)) {
            command.setGroup(group);
        }

        // Update Matches
        if (!"".equals(match)) {
            command.setMatch(match);
        }
        if (!"".equals(match2)) {
            command.setMatch2(match2);
        }

        // Update NotMatches
        if (!"".equals(notMatch)) {
            command.setNotMatch(notMatch);
        }
        if (!"".equals(notMatch2)) {
            command.setNotMatch2(notMatch2);
        }

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Service updateServiceModule(String hostType, String serviceName,
            String moduleName, String context, String login, String password,
            Integer port) {

        // Check Service and Module
        if ("".equals(serviceName) || "".equals(moduleName)) {
            return null;
        }

        // Open Host
        Host host = release.getHostByType(hostType);
        if (host == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType);
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Module
        Module module = service.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, service.getServiceName()});
            return null;
        }

        // Set Context
        if (!"".equals(context)) {
            module.setContext(context);
        }

        // Set Login
        if (!"".equals(login)) {
            module.setLogin(login);
        }

        // Encrypt password
        String encrypted = ConfigService.encryptBlowfish(password);

        // If password encrypted
        if (encrypted != null) {
            module.setPassword(encrypted);
        }

        // Set Port
        if (port != null) {
            module.setPort(port);
        }

        // Save Release
        saveRelease();

        // Return updated Service
        return service;
    }

    public Boolean updateServicePassword(String login, String oldPassword,
            String newPassword) {

        // Check all service
        for (Host host : release.getHosts()) {

            for (Service service : host.getServices()) {

                // Skip irrelevant services
                if (!service.getLogin().equals(login) || !ConfigService.decryptBlowfish(service.getPassword()).equals(oldPassword)) {
                    continue;
                }

                // Update Service
                service.setPassword(ConfigService.encryptBlowfish(newPassword));
            }
        }

        // Save Release
        saveRelease();

        // Return
        return true;
    }

    public Boolean updateSiteHost(String hostName, String hostType) {

        // Check Host Name
        if ("".equals(hostName)) {
            return false;
        }

        // Looking for a Site entity...
        Site site = release.getSite();
        if (site == null) {
            logger.warning("Site configuration is not found in Release");
            return false;
        }

        // Looking for a Host entity...
        Host host = site.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "Host {0} is not found in Site Release", hostName);
            return false;
        }

        // Update Host
        host.setHostType(hostType);

        // Update configuration
        saveRelease();

        // Return True
        return true;
    }

    public final Release uploadStream(String releaseName,
            InputStream releaseStream, String filename) {

        // Check if XML
        Matcher matcher = Pattern.compile("xml$", Pattern.CASE_INSENSITIVE).matcher(filename);
        if (matcher != null && matcher.find()) {

            try {
                // create JAXB context and instantiate marshaller
                JAXBContext context = JAXBContext.newInstance(Release.class);
                Unmarshaller um = context.createUnmarshaller();

                // Get Release
                release = (Release) um.unmarshal(releaseStream);
                release.setReleaseName(releaseName);

                // Save
                return saveRelease();

            } catch (JAXBException ex) {
                logger.log(Level.WARNING, "Release {0} JAXB: {1}", new Object[]{releaseName, ex});
                return null;
            }
        }

        // Check if Json
        matcher = Pattern.compile("json$", Pattern.CASE_INSENSITIVE).matcher(filename);
        if (matcher != null && matcher.find()) {

            // Read JSON from Stream
            try {
                ObjectMapper mapper = new ObjectMapper();
                release = mapper.readValue(releaseStream, Release.class);
                release.setReleaseName(releaseName);

                // Return
                return saveRelease();
            } catch (JsonGenerationException ex) {
                logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
            } catch (JsonMappingException ex) {
                logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Release ObjectMapper: {0}", ex);
            }

        }

        // Unsupported
        logger.warning("Unsupported Release File type uploaded");

        // Nul
        return null;
    }
}
