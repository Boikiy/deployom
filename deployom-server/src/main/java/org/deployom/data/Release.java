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
package org.deployom.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Release {

    private final List<Connection> connections;
    private final List<Flow> flows;
    private final List<Host> hosts;
    private final List<Job> jobs;
    private String releaseName;
    private Site site;
    private Integer version = 1;

    public Release() {
        hosts = new ArrayList<Host>();
        flows = new ArrayList<Flow>();
        jobs = new ArrayList<Job>();
        site = new Site();
        connections = new ArrayList<Connection>();
    }

    public Boolean addConnection(Connection connection) {

        if (getConnection(connection.getConnectionName()) != null) {
            return false;
        }

        connections.add(connection);
        return true;
    }

    public Boolean addFlow(Flow flow) {

        if (getFlow(flow.getFlowName()) != null) {
            return false;
        }

        flows.add(flow);
        return true;
    }

    public Boolean addHost(Host host) {

        if (getHostByType(host.getHostType()) != null) {
            return false;
        }

        hosts.add(host);
        return true;
    }

    public Boolean addJob(Job job) {

        if (getJob(job.getJobName()) != null) {
            return false;
        }

        jobs.add(job);
        return true;
    }

    public Connection getConnection(String connectionName) {
        for (Connection connection : connections) {
            if (connection.getConnectionName().equals(connectionName)) {
                return connection;
            }
        }

        return null;
    }

    @XmlElement(name = "connection")
    public List<Connection> getConnections() {
        return connections;
    }

    public Flow getFlow(String flowName) {
        for (Flow flow : flows) {
            if (flow.getFlowName().equals(flowName)) {
                return flow;
            }
        }

        return null;
    }

    @XmlElement(name = "flow")
    public List<Flow> getFlows() {
        return flows;
    }

    public Host getHostByType(String hostType) {
        for (Host host : hosts) {
            if (host.getHostType().equals(hostType)) {
                return host;
            }
        }

        return null;
    }

    @XmlElement(name = "host")
    public List<Host> getHosts() {
        return hosts;
    }

    public Job getJob(String jobName) {
        for (Job job : jobs) {
            if (job.getJobName().equals(jobName)) {
                return job;
            }
        }

        return null;
    }

    @XmlElement(name = "job")
    public List<Job> getJobs() {
        return jobs;
    }

    public String getReleaseName() {
        return this.releaseName;
    }

    public Site getSite() {

        return site;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Boolean removeConnection(Connection connection) {
        connections.remove(connection);
        return true;
    }

    public Boolean removeFlow(Flow flow) {
        flows.remove(flow);
        return true;
    }

    public Boolean removeHost(Host host) {
        hosts.remove(host);
        return true;
    }

    public Boolean removeJob(Job job) {
        jobs.remove(job);
        return true;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.releaseName;
    }
}
