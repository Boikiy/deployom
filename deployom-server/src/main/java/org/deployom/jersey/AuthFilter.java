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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.deployom.core.ConfigService;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());

    @Context
    HttpServletResponse response;

    @Override
    public void filter(ContainerRequestContext request) {

        // Get Cookie
        Cookie userName = request.getCookies().get("userName");
        Cookie password = request.getCookies().get("password");

        // Check userName and password Cookies
        if (userName == null || password == null || "".equals(userName.getValue())) {
            // No Cookies
            logger.log(Level.WARNING, "No Auth Cookies received, Header: {0}", request.getHeaders());
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        // Open config
        ConfigService configService = new ConfigService();

        // Login
        if (configService.loginUser(userName.getValue(), password.getValue()) == null) {
            // UnAuthorizied
            logger.log(Level.WARNING, "Unauthorized request to: {0}, Header: {1}",
                    new Object[]{request.getUriInfo().getAbsolutePath(), request.getHeaders()});
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        // Check Origin
        String origin = request.getHeaderString("Origin");

        // Add CORS
        if (origin != null && !"".equals(origin)) {
            // Required for CORS
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }
}
