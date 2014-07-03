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
package org.deployom.jersey;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.deployom.core.HistoryService;
import org.deployom.data.History;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

@Path("/History")
public class HistoryResource {

    @Context
    ServletContext context;

    private final HistoryService historyService = new HistoryService();

    @GET
    @Path("addBroadcaster")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput addBroadcaster() {

        // Get Broadcaster
        SseBroadcaster broadcaster = (SseBroadcaster) context.getAttribute("Broadcaster");

        // Broadcaster not found
        if (broadcaster == null) {
            return null;
        }

        // Add to Broadcast
        EventOutput eventOutput = new EventOutput();
        broadcaster.add(eventOutput);

        // Return
        return eventOutput;
    }

    @POST
    @Path("broadcastMessage")
    @Produces(MediaType.TEXT_PLAIN)
    public String broadcastMessage(@FormParam("Message") String message) {

        // Get Broadcaster
        SseBroadcaster broadcaster = (SseBroadcaster) context.getAttribute("Broadcaster");

        // Broadcaster not found
        if (broadcaster == null) {
            return "Broadcaster Failure";
        }

        // Broadcast
        historyService.broadcastMessage(broadcaster, message);

        // Return
        return "Broadcasted";
    }

    @GET
    @Path("getCommands")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public History getCommands() {

        // Open History
        historyService.openHistory();

        // Remove other data
        historyService.getEvents().clear();

        // Return History
        return historyService.getHistory();
    }

    @GET
    @Path("getEvents")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public History getEvents() {

        // Open History
        historyService.openHistory();

        // Remove other data
        historyService.getCommands().clear();

        // Return History
        return historyService.getHistory();
    }
}
