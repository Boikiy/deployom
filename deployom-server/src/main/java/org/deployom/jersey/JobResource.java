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

import java.util.concurrent.ExecutorService;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.deployom.core.JobService;
import org.deployom.data.Host;
import org.deployom.data.Job;
import org.deployom.servlet.JobThread;

@Path("/Job")
public class JobResource {

    @Context
    ServletContext context;

    @POST
    @Path("getHost")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Host getHost(@FormParam("SiteName") String siteName,
            @FormParam("JobName") String jobName,
            @FormParam("HostName") String hostName) {

        // Open Job
        JobService jobService = new JobService(siteName, jobName);

        // Return Host
        return jobService.getHost(hostName);
    }

    @POST
    @Path("getJob")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Job getJob(@FormParam("SiteName") String siteName,
            @FormParam("JobName") String jobName) {

        // Open Job
        JobService jobService = new JobService(siteName, jobName);

        // Return Job
        return jobService.getJob();
    }

    @POST
    @Path("runJob")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String runJob(@FormParam("SiteName") String siteName,
            @FormParam("JobName") String jobName) {

        // Check Context
        if (context == null) {
            return "Context is empty";
        }

        // Get executors
        ExecutorService jobThread = (ExecutorService) context.getAttribute("JobThread");

        if (jobThread == null) {
            return "JobThread is not found";
        }

        // Submit new Job to Executor
        jobThread.submit(new JobThread(siteName, jobName, context));

        // Return
        return jobName + " scheduled";
    }

    @POST
    @Path("updateJob")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateJob(@FormParam("SiteName") String siteName,
            @FormParam("JobName") String jobName,
            @FormParam("Enabled") Boolean enabled) {

        // Open Job
        JobService jobService = new JobService(siteName, jobName);

        // Update Enabled
        jobService.setEnabled(enabled);

        // Save Job
        jobService.saveJob();

        return "Updated";
    }
}
