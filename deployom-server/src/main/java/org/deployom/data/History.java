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
public final class History {

    private List<Command> commands;
    private List<Event> events;
    private Integer maxCommands = 100;
    private Integer maxEvents = 100;

    public History() {
        events = new ArrayList<Event>();
        commands = new ArrayList<Command>();
    }

    public Command addCommand(Command command) {

        // Add Command
        commands.add(command);
        return command;
    }

    public Event addEvent(Event event) {

        // Add Event
        events.add(event);
        return event;
    }

    @XmlElement(name = "command")
    public List<Command> getCommands() {
        return commands;
    }

    @XmlElement(name = "event")
    public List<Event> getEvents() {
        return events;
    }

    public Integer getMaxCommands() {
        return maxCommands;
    }

    public Integer getMaxEvents() {
        return maxEvents;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void setMaxCommands(Integer maxCommands) {
        this.maxCommands = maxCommands;
    }

    public void setMaxEvents(Integer maxEvents) {
        this.maxEvents = maxEvents;
    }
}
