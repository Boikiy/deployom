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
public class OracleDatabase {

    private List<OracleCount> counts;
    private String databaseName;
    private String databaseRole;
    private List<OracleLimit> limits;
    private String logMode;
    private String openMode;
    private String platformName;
    private List<OracleSession> sessions;
    private List<OracleTablespace> tablespaces;

    public OracleDatabase() {
        limits = new ArrayList<OracleLimit>();
        counts = new ArrayList<OracleCount>();
        sessions = new ArrayList<OracleSession>();
        tablespaces = new ArrayList<OracleTablespace>();
    }

    public Boolean addCount(OracleCount count) {
        counts.add(count);
        return true;
    }

    public Boolean addLimit(OracleLimit limit) {
        limits.add(limit);
        return true;
    }

    public Boolean addSession(OracleSession session) {

        sessions.add(session);
        return true;
    }

    public Boolean addTablespace(OracleTablespace tablespace) {

        tablespaces.add(tablespace);
        return true;
    }

    @XmlElement(name = "count")
    public List<OracleCount> getCounts() {
        return counts;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseRole() {
        return databaseRole;
    }

    @XmlElement(name = "limit")
    public List<OracleLimit> getLimits() {
        return limits;
    }

    public String getLogMode() {
        return logMode;
    }

    public String getOpenMode() {
        return openMode;
    }

    public String getPlatformName() {
        return platformName;
    }

    @XmlElement(name = "session")
    public List<OracleSession> getSessions() {
        return sessions;
    }

    @XmlElement(name = "tablespace")
    public List<OracleTablespace> getTablespaces() {
        return tablespaces;
    }

    public void setCounts(List<OracleCount> counts) {
        this.counts = counts;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setDatabaseRole(String databaseRole) {
        this.databaseRole = databaseRole;
    }

    public void setLimits(List<OracleLimit> limits) {
        this.limits = limits;
    }

    public void setLogMode(String logMode) {
        this.logMode = logMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setSessions(List<OracleSession> sessions) {
        this.sessions = sessions;
    }

    public void setTablespaces(List<OracleTablespace> tablespaces) {
        this.tablespaces = tablespaces;
    }

    @Override
    public String toString() {
        return this.databaseName;
    }
}
