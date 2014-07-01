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
public class Command {

    private String commandId;
    private String datetime;
    private Boolean error = false;
    private String exec;
    private String group;
    private String hostName;
    private String match;
    private String match2;
    private String notMatch;
    private String notMatch2;
    private String out;
    private String serviceName;
    private String siteName;
    private Integer timeout;
    private String title;
    private String userName;

    public Command() {
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

    public String getGroup() {
        return this.group;
    }

    public String getHostName() {
        return hostName;
    }

    public String getMatch() {
        return this.match;
    }

    public String getMatch2() {
        return this.match2;
    }

    public String getNotMatch() {
        return this.notMatch;
    }

    public String getNotMatch2() {
        return this.notMatch2;
    }

    public String getOut() {
        return this.out;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getSiteName() {
        return siteName;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUserName() {
        return userName;
    }

    public Boolean isError() {
        return error;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public void setMatch2(String match2) {
        this.match2 = match2;
    }

    public void setNotMatch(String notMatch) {
        this.notMatch = notMatch;
    }

    public void setNotMatch2(String notMatch2) {
        this.notMatch2 = notMatch2;
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

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return this.commandId;
    }
}
