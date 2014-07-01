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
public class Site {

    private Boolean enabled = true;
    private final List<Flow> flows;
    private final List<Host> hosts;
    private final List<Job> jobs;
    private final List<Module> modules;
    private String releaseName;
    private String serverURL;
    private String siteName;
    private Integer version = 1;

    public Site() {
        hosts = new ArrayList<Host>();
        jobs = new ArrayList<Job>();
        flows = new ArrayList<Flow>();
        modules = new ArrayList<Module>();
    }

    public Boolean addFlow(Flow flow) {

        if (getFlow(flow.getFlowName()) != null) {
            return false;
        }

        flows.add(flow);
        return true;
    }

    public Boolean addHost(Host host) {

        if (getHost(host.getHostName()) != null) {
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

    public Boolean addModule(Module module) {

        if (getModule(module.getModuleName()) != null) {
            return false;
        }

        modules.add(module);
        return true;
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

    // Get Host by HostName
    public Host getHost(String hostName) {
        for (Host host : hosts) {
            if (host.getHostName().equals(hostName)) {
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

    public Module getModule(String moduleName) {
        for (Module module : modules) {
            if (module.getModuleName().equals(moduleName)) {
                return module;
            }
        }

        return null;
    }

    @XmlElement(name = "module")
    public List<Module> getModules() {
        return modules;
    }

    public String getReleaseName() {
        return this.releaseName;
    }

    public String getServerURL() {
        return this.serverURL;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean removeHost(Host host) {
        hosts.remove(host);
        return true;
    }

    public Boolean removeModule(Module module) {
        modules.remove(module);
        return true;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.siteName;
    }
}
