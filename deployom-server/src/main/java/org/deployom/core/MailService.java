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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.deployom.data.Module;
import org.deployom.data.User;

public class MailService {

    private static final Logger logger = Logger.getLogger(MailService.class.getName());
    private final ConfigService configService = new ConfigService();
    private final Properties properties;
    private Session session;

    public MailService() {

        // Set properties
        properties = new Properties();

        // Get Mail
        Module module = configService.getModule("mail");
        if (module == null) {
            logger.finest("Mail module is not found. Mail notification disabled.");
            return;
        }

        // Check IP
        if (module.getIP() == null) {
            logger.finest("Mail IP is not set. Mail notification disabled.");
            return;
        }

        // Set Host
        properties.put("mail.smtp.host", module.getIP());

        // Set Port
        if (module.getPort() != null) {
            properties.put("mail.smtp.port", module.getPort());
        }

        // Create Session
        session = Session.getInstance(properties);

    }

    public void sendMail(String subject, String text) {

        // Check if session created
        if (session == null) {
            return;
        }

        try {
            // Create Message
            Message message = new MimeMessage(session);

            // Add To and From
            for (User user : configService.getUsers()) {

                // Get Email
                String email = user.getEmail();

                // Skip users with empty emails
                if (email == null || "".equals(email)) {
                    continue;
                }

                // Add Admin to From
                if ("admin".equals(user.getUserName())) {
                    message.setFrom(new InternetAddress(email));
                    continue;
                }

                // Add recipient
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            // Set Subject and Text
            message.setSubject(subject);
            message.setText(text);

            // Send
            Transport.send(message);

        } catch (MessagingException ex) {
            logger.log(Level.WARNING, "Mail: {0}", ex);
        }
    }
}
