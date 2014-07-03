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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.deployom.core.ConfigService;
import org.deployom.core.OracleService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Host;
import org.deployom.data.Module;
import org.deployom.data.OracleCount;
import org.deployom.data.OracleDatabase;
import org.deployom.data.OracleLimit;
import org.deployom.data.OracleSegment;
import org.deployom.data.OracleSession;
import org.deployom.data.OracleTablespace;
import org.deployom.data.Service;

@Path("/Oracle")
public class OracleResource {

    private static final Logger logger = Logger.getLogger(OracleResource.class.getName());

    @POST
    @Path("getDataFiles")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleDatabase getDataFiles(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {
            // Get Database
            OracleDatabase oracleDatabase = oracleService.getDatabase();

            // If limits are empty
            if (oracleDatabase == null) {
                logger.log(Level.WARNING, "SQL: {0} ({1}) is not reachable", new Object[]{module.getContext(), host.getIP()});

                return null;
            }

            // Get Tablespaces
            List<OracleTablespace> tablespaces = oracleService.getDataFiles();
            if (tablespaces != null) {
                oracleDatabase.setTablespaces(tablespaces);
            }

            // Return
            return oracleDatabase;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }

    @POST
    @Path("getLastAnalyzed")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleDatabase getLastAnalyzed(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {
            // Get Database
            OracleDatabase oracleDatabase = oracleService.getDatabase();

            // If limits are empty
            if (oracleDatabase == null) {
                logger.log(Level.WARNING, "SQL: {0} ({1}) is not reachable", new Object[]{module.getContext(), host.getIP()});

                return null;
            }

            // Get Counts
            List<OracleCount> counts = oracleService.getLastAnalyzed();
            if (counts != null) {
                oracleDatabase.setCounts(counts);
            }

            // Return
            return oracleDatabase;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }

    @POST
    @Path("getLongRunning")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleDatabase getLongRunning(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {
            // Get Database
            OracleDatabase oracleDatabase = oracleService.getDatabase();

            // If limits are empty
            if (oracleDatabase == null) {
                logger.log(Level.WARNING, "SQL: {0} ({1}) is not reachable", new Object[]{module.getContext(), host.getIP()});

                return null;
            }

            // Get Sessions
            List<OracleSession> sessions = oracleService.getLongRunning();
            if (sessions != null) {
                oracleDatabase.setSessions(sessions);
            }

            // Return
            return oracleDatabase;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }

    @POST
    @Path("getSegments")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleTablespace getSegments(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName,
            @FormParam("TablespaceName") String tablespaceName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {

            // New Tablespace
            OracleTablespace oracleTablespace = new OracleTablespace();
            oracleTablespace.setTablespaceName(tablespaceName);

            // Get Segments
            List<OracleSegment> segments = oracleService.getSegments(tablespaceName);
            if (segments != null) {
                oracleTablespace.setSegments(segments);
            }

            // Return
            return oracleTablespace;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }

    @POST
    @Path("getSessions")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleDatabase getSessions(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {
            // Get Database
            OracleDatabase oracleDatabase = oracleService.getDatabase();

            // If limits are empty
            if (oracleDatabase == null) {
                logger.log(Level.WARNING, "SQL: {0} ({1}) is not reachable", new Object[]{module.getContext(), host.getIP()});

                return null;
            }

            // Get Limits
            List<OracleLimit> limits = oracleService.getLimits();
            if (limits != null) {
                oracleDatabase.setLimits(limits);
            }

            // Get Sessions
            List<OracleSession> sessions = oracleService.getSessions();
            if (sessions != null) {
                oracleDatabase.setSessions(sessions);
            }

            // Return
            return oracleDatabase;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }

    @POST
    @Path("getTablespaces")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public OracleDatabase getTablespaces(
            @FormParam("SiteName") String siteName,
            @FormParam("HostName") String hostName,
            @FormParam("ServiceName") String serviceName,
            @FormParam("ModuleName") String moduleName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Open Host
        Host host = siteService.getHost(hostName);
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, siteService.getSiteName()});
            return null;
        }

        // Open Service
        Service service = host.getService(serviceName);
        if (service == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, host.getHostName()});
            return null;
        }

        // Open Host
        Host hostRelease = releaseService.getHost(host.getHostType());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", host.getHostType());
            return null;
        }

        // Open Service
        Service serviceRelease = hostRelease.getService(serviceName);
        if (serviceRelease == null) {
            logger.log(Level.WARNING, "Service {0} is not found in Host {1}", new Object[]{serviceName, hostRelease.getHostType()});
            return null;
        }

        // Open Module
        Module module = serviceRelease.getModule(moduleName);
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found in Service {1}", new Object[]{moduleName, serviceRelease.getServiceName()});
            return null;
        }

        // Decrypt password
        String password = ConfigService.decryptBlowfish(module.getPassword());

        // Oracle
        OracleService oracleService = new OracleService(host.getIP(), module.getContext(), module.getPort(), module.getLogin(), password);

        try {
            // Get Database
            OracleDatabase oracleDatabase = oracleService.getDatabase();

            // If limits are empty
            if (oracleDatabase == null) {
                logger.log(Level.WARNING, "SQL: {0} ({1}) is not reachable", new Object[]{module.getContext(), host.getIP()});

                return null;
            }

            // Get Tablespaces
            List<OracleTablespace> tablespaces = oracleService.getTablespaces();
            if (tablespaces != null) {
                oracleDatabase.setTablespaces(tablespaces);
            }

            // Return
            return oracleDatabase;

        } finally {
            // Close Connection
            oracleService.closeConnection();
        }
    }
}
