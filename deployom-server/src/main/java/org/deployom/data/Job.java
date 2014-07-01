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
public class Job {

    private Boolean enabled = false;
    private String finished;
    private final List<Host> hosts;
    private String jobName;
    private Integer period = 0;
    private Boolean running = false;
    private String siteName;
    private Integer start = 0;

    public Job() {
        hosts = new ArrayList<Host>();
    }

    public Boolean addHost(Host host) {

        if (getHost(host.getHostName()) != null) {
            return false;
        }

        hosts.add(host);
        return true;
    }

    public String getFinished() {
        return this.finished;
    }

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

    public String getJobName() {
        return this.jobName;
    }

    public Integer getPeriod() {
        return this.period;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public Integer getStart() {
        return this.start;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean isRunning() {
        return running;
    }

    public Boolean removeHost(Host host) {
        hosts.remove(host);
        return true;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return this.siteName + "." + this.jobName;
    }
}
