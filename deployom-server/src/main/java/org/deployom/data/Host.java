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
public class Host {

    private String IP;
    private List<Event> events;
    private String hostName;
    private String hostType;
    private String info;

    private List<Service> services;

    public Host() {
        services = new ArrayList<Service>();
        events = new ArrayList<Event>();
    }

    public Event addEvent(String jobName, String serviceName, String commandId) {

        // Skip if event already exist
        if (getEvent(serviceName, commandId) != null) {
            return null;
        }

        // New Event
        Event event = new Event();
        event.setServiceName(serviceName);
        event.setCommandId(commandId);
        event.setJobName(jobName);

        // Add Event
        events.add(event);
        return event;
    }

    public Service addService(String serviceName) {

        // New Service
        Service service = new Service();
        service.setServiceName(serviceName.toUpperCase());

        return addService(service);
    }

    public Service addService(Service service) {

        // Skip if service already exist and not uniq for hostname
        if (getService(service.getServiceName()) != null && service.getHostName() == null) {
            return null;
        }

        services.add(service);
        return service;
    }

    public Event getEvent(String serviceName, String commandId) {
        for (Event event : events) {
            if (event.getServiceName().equals(serviceName) && event.getCommandId().equals(commandId)) {
                return event;
            }
        }

        return null;
    }

    @XmlElement(name = "event")
    public List<Event> getEvents() {
        return events;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getHostType() {
        return this.hostType;
    }

    public String getIP() {
        return this.IP;
    }

    public String getInfo() {
        return info;
    }

    public Service getService(String serviceName) {
        for (Service service : services) {
            if (service.getServiceName().equals(serviceName)) {
                return service;
            }
        }

        return null;
    }

    public Service getServiceByHostName(String hostName) {
        for (Service service : services) {
            if (service.getHostName().equals(hostName)) {
                return service;
            }
        }

        return null;
    }

    @XmlElement(name = "service")
    public List<Service> getServices() {
        return services;
    }

    public Boolean removeEvent(String serviceName, String commandId) {
        Event event = getEvent(serviceName, commandId);

        if (event != null) {
            events.remove(event);
        }

        return true;
    }

    public Boolean removeService(String serviceName) {
        Service service = getService(serviceName);
        services.remove(service);
        return true;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return this.hostName;
    }
}
