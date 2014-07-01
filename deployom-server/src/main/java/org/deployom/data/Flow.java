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
public class Flow {

    private String filter = "";
    private String flowName;
    private final List<Host> hosts;

    public Flow() {
        this.hosts = new ArrayList<Host>();
    }

    public Boolean addHost(Host host) {

        if (getHost(host.getHostName()) != null) {
            return false;
        }

        hosts.add(host);
        return true;
    }

    public String getFilter() {
        return this.filter;
    }

    public String getFlowName() {
        return this.flowName;
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

    public Boolean removeHost(Host host) {
        hosts.remove(host);
        return true;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    @Override
    public String toString() {
        return this.flowName;
    }
}
