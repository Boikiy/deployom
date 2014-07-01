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
public class Service {

    private String IP;
    private List<Chart> charts;
    private List<Command> commands;
    private String hostName;
    private String login;
    private List<Module> modules;
    private Boolean online = false;
    private String password;
    private String serviceLink;
    private String serviceName;

    public Service() {
        charts = new ArrayList<Chart>();
        commands = new ArrayList<Command>();
        modules = new ArrayList<Module>();
    }

    public Service(Service service) {
        IP = service.IP;
        charts = service.charts;
        commands = service.commands;
        hostName = service.hostName;
        login = service.login;
        online = service.online;
        password = service.password;
        modules = service.modules;
        serviceLink = service.serviceLink;
        serviceName = service.serviceName;
    }

    public Boolean addChart(Chart chart) {

        if (getChart(chart.getChartId()) != null) {
            return false;
        }

        charts.add(chart);
        return true;
    }

    public Boolean addCommand(Command command) {

        if (getCommand(command.getCommandId()) != null) {
            return false;
        }

        commands.add(command);
        return true;
    }

    public Boolean addModule(Module module) {

        if (getModule(module.getModuleName()) != null) {
            return false;
        }

        modules.add(module);
        return true;
    }

    public Chart getChart(String chartId) {
        for (Chart chart : charts) {
            if (chart.getChartId().equals(chartId)) {
                return chart;
            }
        }

        return null;
    }

    @XmlElement(name = "chart")
    public List<Chart> getCharts() {
        return charts;
    }

    public Command getCommand(String commandId) {
        for (Command command : commands) {
            if (command.getCommandId().equals(commandId)) {
                return command;
            }
        }

        return null;
    }

    @XmlElement(name = "command")
    public List<Command> getCommands() {
        return commands;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getIP() {
        return this.IP;
    }

    public String getLogin() {
        return this.login;
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

    public String getPassword() {
        return this.password;
    }

    public String getServiceLink() {
        return this.serviceLink;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public Boolean isOnline() {
        return online;
    }

    public Boolean removeChart(Chart chart) {
        charts.remove(chart);
        return true;
    }

    public Boolean removeCommand(Command command) {
        commands.remove(command);
        return true;
    }

    public Boolean removeModule(Module module) {
        modules.remove(module);
        return true;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServiceLink(String serviceLink) {
        this.serviceLink = serviceLink;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return this.serviceName;
    }
}
