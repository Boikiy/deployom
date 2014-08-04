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

import java.io.IOException;
import java.io.RandomAccessFile;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.grizzly.http.server.HttpServer;

@Path("/Server")
public class ServerResource {

    @Context
    ServletContext context;

    @POST
    @Path("getLog")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getLog(@FormParam("LogFile") String logFile) {

        String serverLog = "";
        try {
            RandomAccessFile randomFile = new RandomAccessFile(logFile, "r");
            long startPosition = randomFile.length() - (200 * 100);

            // If file is small
            if (startPosition < 0) {
                startPosition = 0;
            }

            // Seek
            randomFile.seek(startPosition);

            // Read file
            String line;
            while ((line = randomFile.readLine()) != null) {
                serverLog += line + '\n';
            }

            // Close
            randomFile.close();

        } catch (IOException ex) {
            return "Log File: " + ex;
        }

        // Return output
        return serverLog;
    }

    @GET
    @Path("stopServer")
    @Produces(MediaType.TEXT_PLAIN)
    public String stopServer() {

        // Get Server
        HttpServer server = (HttpServer) context.getAttribute("Server");

        // If server not found
        if (server == null) {
            return "Server instance is not found";
        }

        // Shutdown
        server.shutdownNow();

        // Return output
        return "Server stopped";
    }
}
