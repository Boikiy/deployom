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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import org.deployom.data.WeblogicServer;
import org.deployom.data.WeblogicSource;

public class WeblogicService {

    private static final Logger logger = Logger.getLogger(WeblogicService.class.getName());
    private final String IP;
    private JMXConnector connector;
    private final String login;
    private final String password;
    private final Integer port;
    private final String runtimeName = "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean";

    public WeblogicService(String IP, Integer port, String login,
            String password) {
        this.IP = IP;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    public void closeConnector() {
        try {
            connector.close();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        }

        logger.fine("Weblogic Session disconnected.");
    }

    public List<WeblogicServer> getJDBC() {

        // Initialize
        List<WeblogicServer> servers = new ArrayList<WeblogicServer>();

        // Open Connector
        if (openConnector(IP, port, login, password) == null) {
            return null;
        }

        try {
            ObjectName runtimeService = new ObjectName(runtimeName);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName[] serverRunTimes = (ObjectName[]) connection.getAttribute(runtimeService, "ServerRuntimes");

            // Sort Servers
            Arrays.sort(serverRunTimes);

            for (ObjectName serverRunTime : serverRunTimes) {

                // Set Server
                WeblogicServer server = new WeblogicServer();
                server.setServerName((String) connection.getAttribute(serverRunTime, "Name"));
                server.setState((String) connection.getAttribute(serverRunTime, "State"));
                server.setOpenSocket((Integer) connection.getAttribute(serverRunTime, "OpenSocketsCurrentCount"));

                // Add into List
                servers.add(server);

                // Get JDBC
                ObjectName jdbcRuntime = (ObjectName) connection.getAttribute(serverRunTime, "JDBCServiceRuntime");

                // get all server runtimes
                ObjectName[] dataSources = (ObjectName[]) connection.getAttribute(jdbcRuntime, "JDBCDataSourceRuntimeMBeans");

                // print attributes for each server
                for (ObjectName dataSource : dataSources) {

                    // Set Data Source
                    WeblogicSource source = new WeblogicSource();
                    source.setSourceName((String) connection.getAttribute(dataSource, "Name"));
                    source.setCurrent((Integer) connection.getAttribute(dataSource, "ActiveConnectionsCurrentCount"));
                    source.setHigh((Integer) connection.getAttribute(dataSource, "ActiveConnectionsHighCount"));
                    source.setFailures((Integer) connection.getAttribute(dataSource, "FailuresToReconnectCount"));
                    source.setLeaked((Integer) connection.getAttribute(dataSource, "LeakedConnectionCount"));
                    source.setCapacity((Integer) connection.getAttribute(dataSource, "CurrCapacity"));
                    source.setAvailable((Integer) connection.getAttribute(dataSource, "NumAvailable"));
                    source.setWaiting((Integer) connection.getAttribute(dataSource, "WaitingForConnectionCurrentCount"));

                    // Add into Service
                    server.addDataSource(source);
                }
            }

        } catch (IOException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (MalformedObjectNameException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (MBeanException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (AttributeNotFoundException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (InstanceNotFoundException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (ReflectionException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } finally {
            // Close Connector
            closeConnector();
        }

        // Return
        return servers;
    }

    public List<WeblogicServer> getServers() {

        // Initialize
        List<WeblogicServer> servers = new ArrayList<WeblogicServer>();

        // Open Connector
        if (openConnector(IP, port, login, password) == null) {
            return null;
        }

        try {
            ObjectName runtimeService = new ObjectName(runtimeName);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName[] serverRunTimes = (ObjectName[]) connection.getAttribute(runtimeService, "ServerRuntimes");

            // Sort Servers
            Arrays.sort(serverRunTimes);

            for (ObjectName serverRunTime : serverRunTimes) {

                // Set Server
                WeblogicServer server = new WeblogicServer();
                server.setServerName((String) connection.getAttribute(serverRunTime, "Name"));
                server.setState((String) connection.getAttribute(serverRunTime, "State"));
                server.setOpenSocket((Integer) connection.getAttribute(serverRunTime, "OpenSocketsCurrentCount"));

                // Add into List
                servers.add(server);

                // Get Health
                try {
                    String health = (String) connection.getAttribute(serverRunTime, "HealthState").toString();
                    Matcher mHealth = Pattern.compile("State:(\\S+),MBean", Pattern.CASE_INSENSITIVE).matcher(health);
                    if (mHealth.find() && !"".equals(mHealth.group(1))) {
                        server.setState(server.getState() + " (" + mHealth.group(1) + ")");
                    }
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "JMX: {0}", ex);
                } catch (AttributeNotFoundException ex) {
                    logger.log(Level.WARNING, "JMX: {0}", ex);
                } catch (InstanceNotFoundException ex) {
                    logger.log(Level.WARNING, "JMX: {0}", ex);
                } catch (MBeanException ex) {
                    logger.log(Level.WARNING, "JMX: {0}", ex);
                } catch (ReflectionException ex) {
                    logger.log(Level.WARNING, "JMX: {0}", ex);
                }

                // Get JVM
                ObjectName jvmRuntime = (ObjectName) connection.getAttribute(serverRunTime, "JVMRuntime");

                // Set Heap
                server.setHeapFree((Long) connection.getAttribute(jvmRuntime, "HeapFreeCurrent") / 1024 / 1024);
                server.setHeapCurrent((Long) connection.getAttribute(jvmRuntime, "HeapSizeCurrent") / 1024 / 1024);
                server.setHeapMax((Long) connection.getAttribute(jvmRuntime, "HeapSizeMax") / 1024 / 1024);
                server.setUptime((Long) connection.getAttribute(jvmRuntime, "Uptime") / 1000 / 60 / 60);

                // Get ThreadPool
                ObjectName threadRuntime = (ObjectName) connection.getAttribute(serverRunTime, "ThreadPoolRuntime");

                // Set Thread
                server.setThreadIdle((Integer) connection.getAttribute(threadRuntime, "ExecuteThreadIdleCount"));
                server.setThreadTotal((Integer) connection.getAttribute(threadRuntime, "ExecuteThreadTotalCount"));
                server.setThreadHogging((Integer) connection.getAttribute(threadRuntime, "HoggingThreadCount"));
                server.setThreadQueue((Integer) connection.getAttribute(threadRuntime, "QueueLength"));
                server.setThroughput((Double) connection.getAttribute(threadRuntime, "Throughput"));

                // Get JTA
                ObjectName jtaRuntime = (ObjectName) connection.getAttribute(serverRunTime, "JTARuntime");

                // Set Transactions
                server.setTransactionActive((Integer) connection.getAttribute(jtaRuntime, "ActiveTransactionsTotalCount"));
                server.setTransactionTotal((Long) connection.getAttribute(jtaRuntime, "TransactionTotalCount"));
                server.setTransactionCommitted((Long) connection.getAttribute(jtaRuntime, "TransactionCommittedTotalCount"));
                server.setTransactionRolledBack((Long) connection.getAttribute(jtaRuntime, "TransactionRolledBackTotalCount"));
            }

        } catch (AttributeNotFoundException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (InstanceNotFoundException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (MBeanException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (MalformedObjectNameException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (ReflectionException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        } finally {
            // Close Connector
            closeConnector();
        }

        // Return
        return servers;
    }

    public JMXConnector openConnector(String IP, Integer port, String login,
            String password) {

        if (connector != null) {
            return connector;
        }

        try {

            JMXServiceURL serviceURL = new JMXServiceURL("t3", IP, port, "/jndi/weblogic.management.mbeanservers.domainruntime");

            HashMap<String, String> hashtable = new HashMap<String, String>();
            hashtable.put(Context.SECURITY_PRINCIPAL, login);
            hashtable.put(Context.SECURITY_CREDENTIALS, password);
            hashtable.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");

            logger.log(Level.FINE, "Connecting to {0} as {1}", new Object[]{IP, login});
            connector = JMXConnectorFactory.connect(serviceURL, hashtable);

            return connector;

        } catch (Exception ex) {
            logger.log(Level.WARNING, "JMX: {0}", ex);
        }

        return null;
    }
}
