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
package org.deployom.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Config {

    private String envName;
    private final List<Module> modules;
    private String now;
    private final List<Release> releases;
    private final List<Site> sites;
    private String title;
    private final List<User> users;
    private String version;

    public Config() {
        sites = new ArrayList<Site>();
        releases = new ArrayList<Release>();
        users = new ArrayList<User>();
        modules = new ArrayList<Module>();
    }

    public Boolean addModule(Module module) {

        if (getModule(module.getModuleName()) != null) {
            return false;
        }

        modules.add(module);
        return true;
    }

    public Boolean addRelease(Release release) {

        if (getRelease(release.getReleaseName()) != null) {
            return false;
        }

        releases.add(release);
        return true;
    }

    public Boolean addSite(Site site) {

        if (getSite(site.getSiteName()) != null) {
            return false;
        }

        sites.add(site);
        return true;
    }

    public Boolean addUser(User user) {

        if (getUser(user.getUserName()) != null) {
            return false;
        }

        users.add(user);
        return true;
    }

    public String getEnvName() {
        return this.envName;
    }

    public Module getModule(String moduleName) {

        for (Module module : modules) {
            if (module.getModuleName().equals(moduleName)) {
                return module;
            }
        }

        return null;
    }

    @XmlElement(name = "module")
    public List<Module> getModules() {
        return modules;
    }

    public String getNow() {
        return this.now;
    }

    public Release getRelease(String releaseName) {
        for (Release release : releases) {
            if (release.getReleaseName().equals(releaseName)) {
                return release;
            }
        }

        return null;
    }

    @XmlElement(name = "release")
    public List<Release> getReleases() {
        return releases;
    }

    public Site getSite(String siteName) {
        for (Site site : sites) {
            if (site.getSiteName().equals(siteName)) {
                return site;
            }
        }

        return null;
    }

    @XmlElement(name = "site")
    public List<Site> getSites() {
        return sites;
    }

    public String getTitle() {
        return this.title;
    }

    public User getUser(String userName) {
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }

        return null;
    }

    @XmlElement(name = "user")
    public List<User> getUsers() {
        return users;
    }

    public String getVersion() {
        return this.version;
    }

    public Boolean removeModule(Module module) {
        modules.remove(module);
        return true;
    }

    public Boolean removeRelease(Release release) {
        releases.remove(release);
        return true;
    }

    public Boolean removeSite(Site site) {
        sites.remove(site);
        return true;
    }

    public Boolean removeUser(User user) {
        users.remove(user);
        return true;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.envName;
    }
}
