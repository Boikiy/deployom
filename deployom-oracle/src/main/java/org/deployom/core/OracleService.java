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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;
import org.deployom.data.OracleCount;
import org.deployom.data.OracleDatabase;
import org.deployom.data.OracleLimit;
import org.deployom.data.OracleSegment;
import org.deployom.data.OracleSession;
import org.deployom.data.OracleTablespace;

public class OracleService {

    private static final Logger logger = Logger.getLogger(OracleService.class.getName());
    private final String IP;
    private Connection connection;
    private final String dbName;
    private final String login;
    private final String password;
    private final Integer port;

    public OracleService(String IP, String dbName, Integer port, String login,
            String password) {
        this.IP = IP;
        this.dbName = dbName;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    public void closeConnection() {

        // Closed connection
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "JDBC: {0}", ex);
        }

        logger.fine("Oracle Session disconnected.");
    }

    public List<OracleTablespace> getDataFiles() {

        // Initialize
        List<OracleTablespace> tablespaces = new ArrayList<OracleTablespace>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT tablespace_name, file_name, bytes/1024/1024 as currentMb, maxbytes/1024/1024 as maxMb, autoextensible, status FROM dba_data_files order by tablespace_name");

            // Check Results
            while (result.next()) {
                // Add Session
                OracleTablespace tablespace = new OracleTablespace();
                tablespaces.add(tablespace);

                // Set Attributes
                tablespace.setTablespaceName(result.getString("tablespace_name"));
                tablespace.setFileName(result.getString("file_name"));
                tablespace.setCurrentMb(result.getInt("currentMb"));
                tablespace.setMaxMb(result.getInt("maxMb"));
                tablespace.setAutoextensible(result.getString("autoextensible"));
                tablespace.setStatus(result.getString("status"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return tablespaces;
    }

    public OracleDatabase getDatabase() {

        // Initialize
        OracleDatabase database = new OracleDatabase();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select name, log_mode, open_mode, database_role, platform_name from v$database");

            // Check Results
            if (result.next() == true) {

                database.setDatabaseName(result.getString("name"));
                database.setLogMode(result.getString("log_mode"));
                database.setOpenMode(result.getString("open_mode"));
                database.setDatabaseRole(result.getString("database_role"));
                database.setPlatformName(result.getString("platform_name"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return database;
    }

    public List<OracleCount> getLastAnalyzed() {

        // Initialize
        List<OracleCount> counts = new ArrayList<OracleCount>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select NVL(TO_CHAR(trunc(last_analyzed),'YYYY-MM-DD'),'NO STATS') last_analyzed,"
                    + "count(1) \"count\" from user_tables group by trunc(last_analyzed) order by last_analyzed");

            // Check Results
            while (result.next()) {
                // Add Session
                OracleCount count = new OracleCount();
                counts.add(count);

                // Set Attributes
                count.setLastAnalyzed(result.getString("last_analyzed"));
                count.setCount(result.getInt("count"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return counts;
    }

    public List<OracleLimit> getLimits() {

        // Initialize
        List<OracleLimit> limits = new ArrayList<OracleLimit>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select resource_name, current_utilization, max_utilization,"
                    + "initial_allocation, limit_value from v$resource_limit where resource_name in ('processes','sessions')");

            // Check Results
            while (result.next()) {
                // Add limit
                OracleLimit limit = new OracleLimit();
                limits.add(limit);

                // Set Attributes
                limit.setResourceName(result.getString("resource_name"));
                limit.setCurrent(result.getInt("current_utilization"));
                limit.setMax(result.getInt("max_utilization"));
                limit.setInitial(result.getInt("initial_allocation"));
                limit.setLimit(result.getInt("limit_value"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return limits;
    }

    public List<OracleSession> getLongRunning() {

        // Initialize
        List<OracleSession> sessions = new ArrayList<OracleSession>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT s.sid, s.machine, sl.message,sl.elapsed_seconds,sl.time_remaining,"
                    + "ROUND(decode(sl.totalwork,0,0,sl.sofar / sl.totalwork) * 100, 2) progress FROM v$session s, v$session_longops sl "
                    + "WHERE s.SID = sl.SID and decode(sl.totalwork,0,0,sl.sofar / sl.totalwork) < 1 AND s.serial# = sl.serial# "
                    + "ORDER BY progress DESC");

            // Check Results
            while (result.next()) {
                // Add Session
                OracleSession session = new OracleSession();
                sessions.add(session);

                // Set Attributes
                session.setSid(result.getInt("sid"));
                session.setMachine(result.getString("machine"));
                session.setMessage(result.getString("message"));
                session.setElapsedSeconds(result.getInt("elapsed_seconds"));
                session.setRemainingSeconds(result.getInt("time_remaining"));
                session.setProgressPercent(result.getInt("progress"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return sessions;
    }

    public List<OracleSegment> getSegments(String tablespaceName) {

        // Initialize
        List<OracleSegment> segments = new ArrayList<OracleSegment>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            PreparedStatement statement = connection.prepareStatement("select * from (select segment_name, partition_name, segment_type,"
                    + "l.table_name, bytes/1024/1024 as sizeMb from dba_segments s LEFT JOIN dba_lobs l USING (segment_name) "
                    + "where s.tablespace_name=? order by sizeMb desc) where rownum < 20");
            statement.setString(1, tablespaceName);
            ResultSet result = statement.executeQuery();

            // Check Results
            while (result.next()) {
                // Add Segment
                OracleSegment segment = new OracleSegment();
                segments.add(segment);

                // Set Attributes
                segment.setSegmentName(result.getString("segment_name"));
                segment.setPartitionName(result.getString("partition_name"));
                segment.setTableName(result.getString("table_name"));
                segment.setSegmentType(result.getString("segment_type"));
                segment.setSizeMb(result.getInt("sizeMb"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return segments;
    }

    public List<OracleSession> getSessions() {

        // Initialize
        List<OracleSession> sessions = new ArrayList<OracleSession>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT count(1) \"count\", status, machine, program, osuser FROM v$session "
                    + "WHERE username IS NOT NULL GROUP BY status, machine, program, osuser ORDER BY status, machine, program, osuser");

            // Check Results
            while (result.next()) {
                // Add Session
                OracleSession session = new OracleSession();
                sessions.add(session);

                // Set Attributes
                session.setUser(result.getString("osuser"));
                session.setMachine(result.getString("machine"));
                session.setProgram(result.getString("program"));
                session.setStatus(result.getString("status"));
                session.setCount(result.getInt("count"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return sessions;
    }

    public List<OracleTablespace> getTablespaces() {

        // Initialize
        List<OracleTablespace> tablespaces = new ArrayList<OracleTablespace>();

        // Open Connection
        if (openConnection(IP, dbName, port, login, password) == null) {
            return null;
        }

        try {
            // Create Statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT d.tablespace_name as name,ROUND(d.usedsz/1024/1024,0) as currentMb, ROUND(d.maxsz/1024/1024,0) as maxMb,ROUND((d.usedsz-f.freesz)/1024/1024,0) as usedMb,ROUND(100*(d.usedsz-f.freesz)/d.maxsz,0) as usedPercent FROM (SELECT tablespace_name, SUM(DECODE(maxbytes,0,bytes,maxbytes)) maxsz, SUM(bytes) usedsz FROM dba_data_files GROUP BY tablespace_name) d,(SELECT tablespace_name, SUM(bytes) freesz FROM dba_free_space GROUP BY tablespace_name) f WHERE d.tablespace_name = f.tablespace_name (+) ORDER BY 5 DESC");

            // Check Results
            while (result.next()) {
                // Add Session
                OracleTablespace tablespace = new OracleTablespace();
                tablespaces.add(tablespace);

                // Set Attributes
                tablespace.setTablespaceName(result.getString("name"));
                tablespace.setCurrentMb(result.getInt("currentMb"));
                tablespace.setMaxMb(result.getInt("maxMb"));
                tablespace.setUsedMb(result.getInt("usedMb"));
                tablespace.setUsedPercent(result.getInt("usedPercent"));
            }

            // Close
            result.close();
            statement.close();

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL: {0}", ex);
        }

        // Return
        return tablespaces;
    }

    public Connection openConnection(String IP, String dbName, Integer port,
            String login, String password) {

        if (connection != null) {
            return connection;
        }

        try {

            logger.log(Level.FINE, "Connecting to {0}:{1} as {2}", new Object[]{IP, dbName, login});

            OracleDataSource ds = new OracleDataSource();
            ds.setURL("jdbc:oracle:thin:" + login + "/" + password + "@//" + IP + ":" + port + "/" + dbName);
            connection = ds.getConnection();
            if (connection == null) {
                return null;
            }

            // Return
            return connection;
        } catch (Exception ex) {
            logger.log(Level.WARNING, "JDBC: {0}", ex);
        }

        return null;
    }
}
