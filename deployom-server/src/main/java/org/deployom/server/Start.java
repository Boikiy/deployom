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
import javax.servlet.ServletRegistration;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.deployom.core.ConfigService;
import org.deployom.data.Module;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;

public class Start {

    private static Integer CORE_POOL_SIZE = 50;
    public static final String DATA_DIR = "data/";
    private static final String JERSEY_CONTEXT = "/jersey";
    private static Integer MAX_POOL_SIZE = 100;
    private static String SERVER_IP = "0.0.0.0";
    public static Integer SERVER_PORT = 8080;
    private static String STATIC_PATH = "../deployom-web";
    private static final Logger logger = Logger.getLogger(Start.class.getName());

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

        // Initialize Grizzly HttpServer
        HttpServer server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly2", SERVER_IP, SERVER_PORT);
        server.addListener(listener);

        // File Cache
        if ("true".equals(System.getenv("FILE_CACHE_ENABLED"))) {
            listener.getFileCache().setEnabled(true);
        } else {
            listener.getFileCache().setEnabled(false);
        }

        // Static folder
        if (System.getenv("STATIC_PATH") != null) {
            STATIC_PATH = System.getenv("STATIC_PATH");
        }

        // Threads pool
        if (System.getenv("CORE_POOL_SIZE") != null) {
            CORE_POOL_SIZE = Integer.parseInt(System.getenv("CORE_POOL_SIZE"));
        }
        if (System.getenv("MAX_POOL_SIZE") != null) {
            MAX_POOL_SIZE = Integer.parseInt(System.getenv("MAX_POOL_SIZE"));
        }

        // Threads pools
        listener.getTransport().getWorkerThreadPoolConfig().setCorePoolSize(CORE_POOL_SIZE);
        listener.getTransport().getWorkerThreadPoolConfig().setMaxPoolSize(MAX_POOL_SIZE);

        // Initialize Context
        WebappContext context = new WebappContext("context", JERSEY_CONTEXT);

        // Jersey registation
        ServletRegistration servletRegistration = context.addServlet("jersey", ServletContainer.class);
        servletRegistration.setInitParameter("javax.ws.rs.Application", org.deployom.jersey.Application.class.getName());
        servletRegistration.addMapping("/*");

        // Add Listener
        context.addListener(org.deployom.server.Listener.class);

        // Deploy and add into Context
        context.deploy(server);
        context.setAttribute("Server", server);

        // Add Web Static
        server.getServerConfiguration().addHttpHandler(new StaticHttpHandler(STATIC_PATH), "/");

        // Start Server
        try {
            server.start();

            // New client
            final Client client = ClientBuilder.newClient();

            // Checking Server
            try {
                final Response response = client.target("http://" + SERVER_IP + ":" + SERVER_PORT + "/jersey/Config/getConfig")
                        .request()
                        .cookie("userName", module.getLogin())
                        .cookie("password", ConfigService.decryptBlowfish(module.getPassword()))
                        .get();

                // If OK
                if (response.getStatus() == 200) {
                    logger.log(Level.INFO, "Server started, http://{0}:" + SERVER_PORT, SERVER_IP);
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Server failed: {0}", ex);
            } finally {
                // Close Client
                client.close();
            }

            // Wait until Server is running
            while (server.isStarted() == true) {
                Thread.sleep(1000);
            }

        } finally {
            server.shutdownNow();
        }
    }
}
