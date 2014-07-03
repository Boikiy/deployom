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

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.deployom.core.ConfigService;
import org.deployom.core.SiteService;
import org.deployom.data.Config;
import org.deployom.data.User;

@Path("/Config")
public class ConfigResource {

    private final ConfigService configService = new ConfigService();

    @POST
    @Path("addRelease")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addRelease(@FormParam("ReleaseName") String releaseName) {

        // Add Release
        configService.addRelease(releaseName);

        return "Added";
    }

    @POST
    @Path("addModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addModule(@FormParam("ModuleName") String moduleName,
            @FormParam("Context") String context,
            @FormParam("Login") String login,
            @FormParam("Password") String password,
            @FormParam("IP") String IP,
            @FormParam("Port") Integer port) {

        // Add Module
        configService.addModule(moduleName, context, login, password, IP, port);

        return "Added";
    }

    @POST
    @Path("addSiteLocal")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addSiteLocal(@FormParam("SiteName") String siteName,
            @FormParam("ReleaseName") String releaseName,
            @FormParam("HostName") String hostName,
            @FormParam("HostType") String hostType,
            @FormParam("IP") String IP) {

        // Add Local Site
        configService.addSiteLocal(siteName, releaseName);

        // If HostName and HostType specified
        if ("".equals(hostName) || "".equals(hostType)) {
            return "Site added";
        }

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Add Host
        siteService.addHost(hostName, hostType, IP);

        return "Local Site and Host added";
    }

    @POST
    @Path("addSiteRemote")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addSiteRemote(@FormParam("SiteName") String siteName,
            @FormParam("ServerURL") String serverURL) {

        // Add Remote Site
        configService.addSiteRemote(siteName, serverURL);

        return "Added";
    }

    @POST
    @Path("addUser")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUser(@FormParam("UserName") String userName,
            @FormParam("Password") String password,
            @FormParam("Role") String role,
            @FormParam("Email") String email,
            @FormParam("Info") String info) {

        // Add User
        configService.addUser(userName, password, role, email, info);

        return "Added";
    }

    @POST
    @Path("loginUser")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User loginUser(@FormParam("UserName") String userName,
            @FormParam("Password") String password) {

        // Login
        return configService.loginUser(userName, password);
    }

    @GET
    @Path("getConfig")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Config getConfig() {

        // Return Config
        return configService.getConfig();
    }

    @POST
    @Path("removeRelease")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeRelease(@FormParam("ReleaseName") String releaseName) {

        // Remove Release
        configService.removeRelease(releaseName);

        return "Removed";
    }

    @POST
    @Path("removeModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeModule(@FormParam("ModuleName") String moduleName) {

        // Remove Module
        configService.removeModule(moduleName);

        return "Removed";
    }

    @POST
    @Path("removeSite")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeSite(@FormParam("SiteName") String siteName) {

        // Remove Site
        configService.removeSite(siteName);

        return "Removed";
    }

    @POST
    @Path("removeUser")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeUser(@FormParam("UserName") String userName) {

        // Remove User
        configService.removeUser(userName);

        return "Removed";
    }

    @POST
    @Path("updateConfig")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateConfig(@FormParam("EnvName") String envName,
            @FormParam("Title") String title) {

        // Update Configuration
        configService.updateConfig(envName, title);

        return "Updated";
    }

    @POST
    @Path("updateModule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateModule(@FormParam("ModuleName") String moduleName,
            @FormParam("Context") String context,
            @FormParam("Login") String login,
            @FormParam("Password") String password,
            @FormParam("IP") String IP,
            @FormParam("Port") Integer port) {

        // Update Module
        configService.updateModule(moduleName, context, login, password, IP, port);

        return "Updated";
    }

    @POST
    @Path("updateSite")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateSite(@FormParam("SiteName") String siteName,
            @FormParam("Enabled") Boolean enabled) {

        // Update Site
        configService.updateSite(siteName, enabled);

        return "Updated";
    }

    @POST
    @Path("updateUser")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateUser(@FormParam("UserName") String userName,
            @FormParam("Password") String password,
            @FormParam("Role") String role,
            @FormParam("Email") String email,
            @FormParam("Info") String info) {

        // Update User
        configService.updateUser(userName, password, role, email, info);

        return "Updated";
    }
}
