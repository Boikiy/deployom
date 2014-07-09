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

import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.deployom.core.AuditService;
import org.deployom.core.JobService;
import org.deployom.core.ReleaseService;
import org.deployom.core.SiteService;
import org.deployom.data.Job;
import org.deployom.data.Site;

@Path("/Audit")
public class AuditResource {

    @POST
    @Path("downloadAudit")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response downloadSite(@FormParam("SiteName") String siteName) {

        // Audit
        AuditService auditService = new AuditService(siteName);

        // Save Audit
        auditService.saveAudit();

        // Open File
        File file = new File(auditService.getFileName());

        // Create Response
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=" + siteName + ".audit.xls");

        // Return Response
        return response.build();
    }

    @POST
    @Path("getJobs")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Site getJobs(@FormParam("SiteName") String siteName) {

        // Open Site
        SiteService siteService = new SiteService(siteName);

        // Open Release
        ReleaseService releaseService = new ReleaseService(siteService.getReleaseName());

        // Remove Hosts
        siteService.getHosts().clear();

        for (Job job : releaseService.getJobs()) {
            JobService jobService = new JobService(siteName, job.getJobName());

            // Add Job
            siteService.getSite().addJob(jobService.getJob());
        }

        // Return Site
        return siteService.getSite();
    }
}
