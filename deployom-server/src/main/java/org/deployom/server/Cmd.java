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
package org.deployom.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import org.deployom.core.ConfigService;
import org.deployom.data.Module;

public class Cmd {

    private static final Logger logger = Logger.getLogger(Cmd.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get Server Credentials
        ConfigService configService = new ConfigService();
        Module module = configService.getModule("server");

        // Check Module
        if (module == null) {
            logger.log(Level.WARNING, "Server module is not found");
            return;
        }

        // Override IP
        if (module.getIP() != null) {
            Stop.SERVER_IP = module.getIP();
        }

        // Override Port
        if (module.getPort() != null) {
            Start.SERVER_PORT = module.getPort();
        }

        // New client
        final Client client = ClientBuilder.newClient();

        // Arguments
        String arguments = "";
        for (String argument : args) {
            arguments += argument + " ";
        }

        String addHost = "add host -site (\\S+) -type (\\S+) -ip (\\S+) -host (\\S+)";
        Matcher matcher = Pattern.compile(addHost, Pattern.CASE_INSENSITIVE).matcher(arguments);

        // Add Host
        if (matcher != null && matcher.find()) {

            logger.log(Level.INFO, "Adding host, http://{0}:" + Start.SERVER_PORT + "/jersey/Site/addHost", Stop.SERVER_IP);

            // Form
            Form form = new Form();
            form.param("SiteName", matcher.group(1));
            form.param("HostType", matcher.group(2));
            form.param("IP", matcher.group(3));
            form.param("HostName", matcher.group(4));

            // Add Host
            try {

                String result = client.target("http://" + Stop.SERVER_IP + ":" + Start.SERVER_PORT + "/jersey/Site/addHost").request()
                        .cookie("userName", module.getLogin())
                        .cookie("password", ConfigService.decryptBlowfish(module.getPassword()))
                        .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);

                logger.log(Level.INFO, result);

            } catch (Exception ex) {
                logger.log(Level.WARNING, "Adding host", ex);
            } finally {
                // Close Client
                client.close();
            }

            return;
        }

        logger.log(Level.WARNING, "Can''t parse: {0}", arguments);
        System.out.println("Usage:");
        System.out.println(" - Add Host: " + addHost);
    }
}
