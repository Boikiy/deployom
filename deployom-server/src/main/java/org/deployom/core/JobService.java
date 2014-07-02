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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Host;
import org.deployom.data.Job;

public class JobService {

    private Job job;
    private static final Logger logger = Logger.getLogger(JobService.class.getName());

    public JobService(String siteName, String jobName) {

        // New Job
        job = new Job();
        job.setSiteName(siteName);
        job.setJobName(jobName);
        job.setEnabled(true);

        // Open Job
        openJob();
    }

    public Host addHost(String hostName) {

        // Create new Host
        Host host = new Host();
        host.setHostName(hostName);

        // Add Host
        job.addHost(host);

        // Return Host
        return host;
    }

    public Boolean isEnabled() {
        return job.isEnabled();
    }

    public Host getHost(String hostName) {
        return job.getHost(hostName);
    }

    public List<Host> getHosts() {

        return job.getHosts();
    }

    public String getJobName() {
        return job.getJobName();
    }

    public String getFileName() {

        return ConfigService.DATA_DIR + job.toString() + ".job.json";
    }

    public Job getJob() {

        return job;
    }

    public final Job openJob() {

        // Read JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            job = mapper.readValue(new File(getFileName()), Job.class);

            // Return
            return job;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        } catch (FileNotFoundException ex) {
            saveJob();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        }

        return null;
    }

    public void removeHosts() {
        job.getHosts().clear();
    }

    public Job saveJob() {

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), job);
            return job;
        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Job ObjectMapper: {0}", ex);
        }

        return null;
    }

    public void setEnabled(Boolean enabled) {
        job.setEnabled(enabled);
    }

    public void setStart(Integer start) {
        job.setStart(start);
    }

    public void setPeriod(Integer period) {
        job.setPeriod(period);
    }

    public void setFinished(String finished) {
        job.setFinished(finished);
    }

    public void setRunning(Boolean running) {
        job.setRunning(running);
    }
}
