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
package org.deployom.grizzly;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.deployom.core.ConfigService;
import org.deployom.data.Module;

public class Stop {

    private static String SERVER_IP = "127.0.0.1";
    private static Integer SERVER_PORT = 8080;
    private static final Logger logger = Logger.getLogger(Stop.class.getName());

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
            SERVER_IP = module.getIP();
        }

        // Override Port
        if (module.getPort() != null) {
            SERVER_PORT = module.getPort();
        }

        // New client
        final Client client = ClientBuilder.newClient();
        logger.log(Level.INFO, "Server stopping, http://" + SERVER_IP + ":" + SERVER_PORT + "/jersey/Server/stopServer");

        // Stop Server
        try {
            client.target("http://" + SERVER_IP + ":" + SERVER_PORT + "/jersey/Server/stopServer").request()
                    .cookie("userName", module.getLogin())
                    .cookie("password", ConfigService.decryptBlowfish(module.getPassword()))
                    .get();

        } catch (Exception ex) {
            logger.log(Level.INFO, "Server stopped");
        } finally {
            // Close Client
            client.close();
        }
    }
}
