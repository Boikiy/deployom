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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Command;
import org.deployom.data.Event;
import org.deployom.data.History;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;

public class HistoryService {

    private static final Logger logger = Logger.getLogger(HistoryService.class.getName());
    private History history;

    public HistoryService() {
        history = new History();
    }

    public Command addCommand(SseBroadcaster broadcaster, Command command) {

        // Open
        openHistory();

        // Get current time
        Date now = new Date();
        command.setDatetime(now.toString());

        // Add Command
        history.addCommand(command);

        // Save
        saveHistory();

        // Broadcast
        broadcastMessage(broadcaster, "Command '" + command.getTitle() + "' [" + command.getServiceName() + "] on "
                + command.getHostName() + " executed by " + command.getUserName());

        // Return
        return command;
    }

    public Event addEvent(SseBroadcaster broadcaster, Event event) {

        // Open
        openHistory();

        // Get current time
        Date now = new Date();
        event.setDatetime(now.toString());

        // Add Event
        history.addEvent(event);

        // Save
        saveHistory();

        // Broadcast
        broadcastMessage(broadcaster, "Event for " + event.toString() + " added");

        // Return
        return event;
    }

    public void broadcastMessage(SseBroadcaster broadcaster, String message) {

        // If broadcaster failed
        if (broadcaster == null) {
            logger.log(Level.WARNING, "Broadcaster is not Enabled");
            return;
        }

        // Build Event
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        OutboundEvent event = eventBuilder.name("message")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, message)
                .build();

        // Broadcast
        broadcaster.broadcast(event);
    }

    public List<Command> getCommands() {
        return history.getCommands();
    }

    public List<Event> getEvents() {
        return history.getEvents();
    }

    public String getFileName() {

        // Return Path
        return ConfigService.DATA_DIR + "history.json";
    }

    public History getHistory() {

        return history;
    }

    public final History openHistory() {

        // Read JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            history = mapper.readValue(new File(getFileName()), History.class);
            return history;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        } catch (FileNotFoundException ex) {
            saveHistory();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        }

        return null;
    }

    public History saveHistory() {

        // Keep Max Events 
        Iterator<Event> iteratorEvent = history.getEvents().iterator();
        while (iteratorEvent.hasNext() && history.getEvents().size() > history.getMaxEvents()) {
            iteratorEvent.next();
            iteratorEvent.remove();
        }

        // Keep Max Commands 
        Iterator<Command> iteratorCommand = history.getCommands().iterator();
        while (iteratorCommand.hasNext() && history.getCommands().size() > history.getMaxCommands()) {
            iteratorCommand.next();
            iteratorCommand.remove();
        }

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), history);
            return history;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "History ObjectMapper: {0}", ex);
        }

        return null;
    }
}
