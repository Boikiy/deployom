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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Event;
import org.deployom.data.Host;
import org.deployom.data.Service;
import org.deployom.data.Site;
import org.deployom.server.Start;

public class SiteService {

    private static final Logger logger = Logger.getLogger(SiteService.class.getName());
    private ReleaseService releaseService;
    private Site site;

    public SiteService(String siteName) {

        // Create new Site
        site = new Site();
        site.setSiteName(siteName);

        // Open Site
        openSite();
    }

    public Host addHost(String hostName, String hostType, String IP) {

        // Open Release if not opened
        if (releaseService == null) {
            releaseService = new ReleaseService(site.getReleaseName());
        }

        // If Host Name or Type or IP not specified
        if ("".equals(hostName) || "".equals(hostType) || "".equals(IP)) {
            return null;
        }

        // Looking for Host Type
        Host hostRelease = releaseService.getHost(hostType.toUpperCase());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType.toUpperCase());
            return null;
        }

        // Create host
        Host host = new Host();

        // Set Host Type and add OS Service
        host.setHostType(hostRelease.getHostType());
        host.addService("OS");

        // Simplify hostName
        hostName = hostName.replaceAll(ConfigService.HOSTNAME, "$1").replaceAll(ConfigService.PATTERN, "").toLowerCase();

        // Set IP and HostName
        host.setHostName(hostName);
        host.setIP(IP);

        // Add Host
        site.addHost(host);

        // Save updated Site
        saveSite();

        // Return Create Host
        return host;
    }

    public Host addService(String hostName, String serviceName) {

        // Open Host
        Host host = site.getHost(hostName);

        // Check Host
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, site.getSiteName()});
            return null;
        }

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Add service
        host.addService(serviceName);

        // Save Site
        saveSite();

        // Return updated Host
        return host;
    }

    public String getFileName() {

        return Start.DATA_DIR + site.getSiteName() + ".site.json";
    }

    public Host getHost(String hostName) {

        return site.getHost(hostName);
    }

    public List<Host> getHosts() {

        return site.getHosts();
    }

    public String getReleaseName() {

        return site.getReleaseName();
    }

    public Site getSite() {

        return site;
    }

    public String getSiteName() {

        return site.getSiteName();
    }

    public final Site openSite() {

        // Read JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            site = mapper.readValue(new File(getFileName()), Site.class);

            // Return
            return site;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        }

        return null;
    }

    public Site removeEvents() {

        // Check all hosts
        for (Host host : site.getHosts()) {

            // Remove Events
            host.getEvents().clear();
        }

        // Save and Return updated Site
        return saveSite();
    }

    public Site removeHost(String hostName) {

        // Get host from Site
        Host host = site.getHost(hostName);

        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, site.getSiteName()});
            return null;
        }

        // Remove Host
        site.removeHost(host);

        // Save and Return updated Site
        return saveSite();
    }

    public Host removeService(String hostName, String serviceName) {

        // Open Host
        Host host = site.getHost(hostName);

        // Check Host
        if (host == null) {
            logger.log(Level.WARNING, "{0} Host is not found in Site {1}", new Object[]{hostName, site.getSiteName()});
            return null;
        }

        // Check Service Name
        if ("".equals(serviceName)) {
            return null;
        }

        // Remove service
        host.removeService(serviceName);

        // Remove Host Events
        host.getEvents().clear();

        // Save Site
        saveSite();

        // Return updated Host
        return host;
    }

    public Host renameHost(String hostName, String newHostName) {

        // If Host Name or new Host Name not specified
        if ("".equals(hostName) || "".equals(newHostName)) {
            return null;
        }

        // Get Host
        Host host = site.getHost(hostName);

        // Remove Services and set Type and IP
        host.setHostName(newHostName);

        // Save Site
        saveSite();

        // Return updated Host
        return host;
    }

    public Site saveSite() {

        // Sort hosts
        Collections.sort(site.getHosts(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                return h1.toString().compareToIgnoreCase(h2.toString());
            }
        });

        // Check all hosts
        for (Host host : site.getHosts()) {

            // Sort hosts
            Collections.sort(host.getServices(), new Comparator<Service>() {
                @Override
                public int compare(Service s1, Service s2) {
                    return s1.toString().compareToIgnoreCase(s2.toString());
                }
            });

            Collections.sort(host.getEvents(), new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    return e1.toString().compareToIgnoreCase(e2.toString());
                }
            });
        }

        // Update version
        site.setVersion(site.getVersion() + 1);

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), site);
            return site;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
        }

        return null;
    }

    public void setReleaseName(String releaseName) {

        site.setReleaseName(releaseName);
    }

    public Host updateHost(String hostName, String hostType, String IP) {

        // Open Release if not opened
        if (releaseService == null) {
            releaseService = new ReleaseService(site.getReleaseName());
        }

        // If Host Name or Type or IP not specified
        if ("".equals(hostName) || "".equals(hostType) || "".equals(IP)) {
            return null;
        }

        // Looking for Host Type
        Host hostRelease = releaseService.getHost(hostType.toUpperCase());
        if (hostRelease == null) {
            logger.log(Level.WARNING, "Host Type {0} is not found in Release", hostType.toUpperCase());
            return null;
        }

        // Create host
        Host host = site.getHost(hostName);

        // Remove Services and set Type and IP
        host.setHostType(hostType);
        host.setIP(IP);

        // Save Site
        saveSite();

        // Return updated Host
        return host;
    }

    public final Site uploadStream(String siteName, InputStream siteStream,
            String filename) {

        // Check if XML
        Matcher matcher = Pattern.compile("xml$", Pattern.CASE_INSENSITIVE).matcher(filename);
        if (matcher != null && matcher.find()) {

            try {
                // create JAXB context and instantiate marshaller
                JAXBContext context = JAXBContext.newInstance(Site.class);
                Unmarshaller um = context.createUnmarshaller();

                site = (Site) um.unmarshal(siteStream);
                site.setSiteName(siteName);

                // Save
                return saveSite();

            } catch (JAXBException ex) {
                logger.log(Level.WARNING, "Site {0} JAXB: {1}", new Object[]{siteName, ex});
                return null;
            }
        }

        // Check if Json
        matcher = Pattern.compile("json$", Pattern.CASE_INSENSITIVE).matcher(filename);
        if (matcher != null && matcher.find()) {

            // Read JSON from Stream
            try {
                ObjectMapper mapper = new ObjectMapper();
                site = mapper.readValue(siteStream, Site.class);
                site.setSiteName(siteName);

                // Return
                return saveSite();
            } catch (JsonGenerationException ex) {
                logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
            } catch (JsonMappingException ex) {
                logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Site ObjectMapper: {0}", ex);
            }

        }

        // Unsupported
        logger.warning("Unsupported Site File type uploaded");
        return null;
    }
}
