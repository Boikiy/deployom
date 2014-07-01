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

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String commandId;
    private String datetime;
    private String exec;
    private String hostName;
    private String jobName;
    private String out;
    private String serviceName;
    private String siteName;
    private String title;

    public Event() {
    }

    public String getCommandId() {
        return this.commandId;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getExec() {
        return this.exec;
    }

    public String getHostName() {
        return hostName;
    }

    public String getJobName() {
        return this.jobName;
    }

    public String getOut() {
        return out;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return commandId + " [" + serviceName + "] on " + hostName;
    }
}
