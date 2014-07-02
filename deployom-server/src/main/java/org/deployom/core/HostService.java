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

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Command;
import org.deployom.data.Host;
import org.deployom.data.Service;

public class HostService {

    private static final Logger logger = Logger.getLogger(HostService.class.getName());
    private final Host host;
    private final Host hostRelease;
    private final Map<String, Session> sessions;

    public HostService(Host host, Host hostRelease) {
        this.host = host;
        this.hostRelease = hostRelease;
        sessions = new TreeMap<String, Session>();
    }

    public void closeSessions() {
        for (String login : sessions.keySet()) {
            sessions.get(login).disconnect();
            logger.log(Level.FINEST, "Session disconnected to {0} as {1}", new Object[]{host.getHostName(), login});
        }
    }

    /**
     * Execute command on Host
     *
     * @param serviceName
     * @param commandId
     * @return
     */
    public String execCommand(String serviceName, String commandId) {

        // Get Host IP
        String IP = host.getIP();
        if (IP == null) {
            logger.log(Level.WARNING, "IP is not defined for host {0}", host.getHostName());
            return "IP is not defined for host " + host.getHostName();
        }

        // Check service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "{0} service not found in Release", serviceName);
            return serviceName + " service not found in Release";
        }

        // Get Login and Password
        String serviceLogin = serviceRelease.getLogin();
        String servicePassword = ConfigService.decryptBlowfish(serviceRelease.getPassword());

        // Return Null if there is no login/password in Properties
        if (serviceLogin == null || "".equals(serviceLogin) || servicePassword == null || "".equals(servicePassword)) {
            logger.log(Level.WARNING, "{0} service created w/o login/password", serviceName);
            return serviceName + " service created w/o login/password";
        }

        // Set command
        Command serviceCommand = serviceRelease.getCommand(commandId);
        if (serviceCommand == null) {
            logger.log(Level.WARNING, "Command {0} is not found in service {1} [{2}]",
                    new Object[]{commandId, serviceName, host.getHostName()});
            return "Command " + commandId + " is not found in service " + serviceName + " [" + host.getHostName() + "]";
        }

        String commandOutput = "";
        Session session;

        try {
            session = openSession(serviceLogin, servicePassword, IP);
            if (session == null) {
                logger.log(Level.WARNING, "Session: {0} is not reachable", IP);
                return "Session: " + IP + " is not reachable";
            }

            logger.log(Level.FINEST, "Executing command {0}", serviceCommand.getExec());

            // Open Channel
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(serviceCommand.getExec());

            // no input
            channel.setInputStream(null);
            try {
                InputStream stderr = channel.getErrStream();
                InputStream stdout = channel.getInputStream();

                // Set timeout
                Integer timeout = 30;

                if (serviceCommand.getTimeout() != null && serviceCommand.getTimeout() > 0) {
                    timeout = serviceCommand.getTimeout();
                }

                // Connect channel
                channel.connect();

                byte[] tmp = new byte[1024];
                while (timeout-- >= 0) {
                    while (stdout.available() > 0) {
                        int i = stdout.read(tmp, 0, 1024);
                        if (i < 0) {
                            break;
                        }
                        commandOutput += new String(tmp, 0, i);
                    }

                    // If channel closed
                    if (channel.isClosed()) {
                        logger.log(Level.FINEST, "Exit-status: {0}", channel.getExitStatus());
                        break;
                    }

                    // Wait a second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        commandOutput += ex;
                        logger.log(Level.WARNING, "InputStream: {0}", ex);
                    }
                }

                // If timeout happend
                if (timeout <= 0) {
                    logger.log(Level.WARNING, "InputStream: command {0} timeout in service {1} [{2}]",
                            new Object[]{commandId, serviceName, host.getHostName()});
                }

                // Add Error
                commandOutput += stderrToString(stderr);

            } catch (IOException ex) {
                logger.log(Level.WARNING, "InputStream: {0}", ex);
            }

            // Disconnect
            channel.disconnect();

        } catch (JSchException ex) {
            commandOutput += ex;
            logger.log(Level.WARNING, "JSch for {0}: {1}", new Object[]{host.getHostName(), ex});
        }

        // Trim output and illegal characters
        commandOutput = commandOutput.replaceAll(ConfigService.ILLEGAL, "").trim();

        // Dummy message
        if ("".equals(commandOutput)) {
            commandOutput = "No content";
        }

        // Return command output
        return commandOutput;
    }

    public String getFileName() {

        return ConfigService.DATA_DIR + host.toString() + ".host.json";
    }

    public String getHostName() {
        return host.getHostName();
    }

    /**
     * Open session
     *
     * @param login
     * @param password
     * @param IP
     * @return
     */
    public Session openSession(String login, String password, String IP) {

        // Get Session
        Session session = sessions.get(login);
        if (session != null && session.isConnected()) {
            return session;
        }

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(login, IP);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");

            // Debug
            logger.log(Level.FINEST, "Connecting to {0} as {1}", new Object[]{IP, login});

            // 30 seconds timeout
            session.setTimeout(30000);
            session.connect();

            // Add Session
            sessions.put(login, session);

            return session;

        } catch (JSchException ex) {
            logger.log(Level.WARNING, "JSch Open Session for {0}: {1}", new Object[]{host.getHostName(), ex});
        }

        return null;
    }

    public Command runCommand(Service service, String commandId, Host jobHost) {

        // Get ServiceName
        String serviceName = service.getServiceName();

        // Get Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Release''s host {1}",
                    new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Get Command
        Command commandRelease = serviceRelease.getCommand(commandId);
        if (commandRelease == null) {
            logger.log(Level.WARNING, "Command {0} is not found in Release''s service {1}",
                    new Object[]{commandId, serviceRelease.getServiceName()});
            return null;
        }

        // Get existen or create a new Service
        Service serviceJob = jobHost.getService(serviceName);
        if (serviceJob == null) {
            serviceJob = jobHost.addService(serviceName);
        }

        // Create a command
        Command command = serviceJob.getCommand(commandId);

        // If command already executed
        if (command != null) {
            return command;
        }

        // Create a Command
        command = new Command();
        command.setCommandId(commandRelease.getCommandId());
        command.setExec(commandRelease.getExec());
        command.setTitle(commandRelease.getTitle());

        // Debug
        logger.log(Level.FINEST, "Executing command: {0}", commandId);

        // Execute command
        command.setOut(execCommand(serviceName, commandId));

        // Add command
        serviceJob.addCommand(command);
        serviceJob.setOnline(service.isOnline());

        // Add Error String
        String error = "";

        if (commandRelease.getMatch() != null) {

            Matcher matcher = Pattern.compile(commandRelease.getMatch(), Pattern.CASE_INSENSITIVE).matcher(command.getOut());

            // If not match
            if (matcher == null || !matcher.find()) {

                // Set command's error
                command.setError(true);

                // Add Error
                error += "ERROR: Output does not match success expression: " + commandRelease.getMatch() + "\n";

                // Set service offline if online failed
                if ("Online".equals(commandId)) {
                    serviceJob.setOnline(false);
                    service.setOnline(serviceJob.isOnline());
                    logger.log(Level.FINEST, "Service {0} is Offline on {1}", new Object[]{serviceName, getHostName()});
                }

            } else if ("Online".equals(commandId)) {
                serviceJob.setOnline(true);
                service.setOnline(true);
            }
        }

        if (commandRelease.getMatch2() != null) {

            Matcher matcher = Pattern.compile(commandRelease.getMatch2(), Pattern.CASE_INSENSITIVE).matcher(command.getOut());

            // If not match
            if (matcher == null || !matcher.find()) {
                command.setError(true);

                // Add Error
                error += "ERROR: Output does not match success expression: " + commandRelease.getMatch2() + "\n";
            }
        }

        if (commandRelease.getNotMatch() != null) {

            Matcher matcher = Pattern.compile(commandRelease.getNotMatch(), Pattern.CASE_INSENSITIVE).matcher(command.getOut());

            // If match
            if (matcher != null && matcher.find()) {
                command.setError(true);

                // Add Error
                error += "ERROR: Output match error expression: " + commandRelease.getNotMatch() + "\n";
            }
        }

        if (commandRelease.getNotMatch2() != null) {

            Matcher matcher = Pattern.compile(commandRelease.getNotMatch2(), Pattern.CASE_INSENSITIVE).matcher(command.getOut());

            // If match
            if (matcher != null && matcher.find()) {
                command.setError(true);

                // Add Error
                error += "ERROR: Output match error expression: " + commandRelease.getNotMatch2() + "\n";
            }
        }

        // If errors found
        if (!"".equals(error)) {
            command.setOut(error + "\n" + command.getOut());
        }

        // Return true if no errors found
        return command;
    }

    public Host saveHost() {

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), host);
            return host;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Host ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Host ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Host ObjectMapper: {0}", ex);
        }

        return null;
    }

    public String stderrToString(InputStream stderr) {
        Scanner scanner = new Scanner(stderr).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
