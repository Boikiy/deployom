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
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deployom.data.Config;
import org.deployom.data.Module;
import org.deployom.data.Release;
import org.deployom.data.Site;
import org.deployom.data.User;
import org.deployom.server.Start;

public final class ConfigService {

    public static final String HOSTNAME = "([\\w-]+)\\.*.*";
    public static final String ILLEGAL = "[^\u0009\r\n\u0020-\uD7FF\uE000-\uFFFD\ud800\udc00-\udbff\udfff]";
    public static final String PATTERN = "[^A-Za-z0-9_-]+";
    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());

    public static String decryptBlowfish(String password) {

        // If password empty
        if ("".equals(password)) {
            return null;
        }

        try {
            // Create key and cipher
            Key aesKey = new SecretKeySpec("TemplateBlowfish".getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");

            // decrypt the text
            byte[] encrypted = parseBase64Binary(password);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            // Return Decrypted
            return new String(cipher.doFinal(encrypted));
        } catch (InvalidKeyException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (BadPaddingException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (IllegalBlockSizeException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (NoSuchPaddingException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        }

        // Return
        return null;
    }

    public static String encryptBlowfish(String password) {

        // If password empty
        if ("".equals(password)) {
            return null;
        }

        try {
            // Create key and cipher
            Key aesKey = new SecretKeySpec("TemplateBlowfish".getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");

            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(password.getBytes());

            // Return Base64
            return printBase64Binary(encrypted);
        } catch (InvalidKeyException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (BadPaddingException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (IllegalBlockSizeException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        } catch (NoSuchPaddingException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        }

        // Return
        return null;
    }

    private Config config;
    private final String version = "4.081514";

    public ConfigService() {

        // Open
        if (openConfig() == null) {

            // Create new Config
            updateConfig("Local", "DeployOM");
            addUser("admin", "passw0rd", "admin", "", "Administrative user allowed to use Dashboard and do Configuration changes");
            addUser("support", "passw0rd", "support", "", "Support user allowed to use Dashboard");
            addUser("server", "d3pl0y0m", "admin", "", "Administrative user to start/stop Server");
            addModule("server", "", "server", "d3pl0y0m", "0.0.0.0", 8080);
            addModule("mail", "", "", "", "", 25);
        }
    }

    public Boolean addModule(String moduleName, String context, String login,
            String password, String IP, Integer port) {

        // Keep only required symbols
        moduleName = moduleName.replaceAll(ConfigService.PATTERN, "");

        // Check if Module already exist
        if (config.getModule(moduleName) != null) {
            return false;
        }

        // New Module
        Module module = new Module();
        module.setModuleName(moduleName);
        module.setContext(context);
        module.setLogin(login);
        module.setIP(IP);
        module.setPort(port);

        // Encrypt password
        String encrypted = encryptBlowfish(password);

        // If password encrypted
        if (encrypted != null) {
            module.setPassword(encrypted);
        }

        // Add Module
        config.addModule(module);

        // Save Config
        saveConfig();

        // Return
        return true;
    }

    public Boolean addRelease(String releaseName) {

        // Keep only required symbols
        releaseName = releaseName.replaceAll(ConfigService.PATTERN, "");

        // Check if Release already exist
        if (config.getRelease(releaseName) != null) {
            return false;
        }

        // New Release
        Release release = new Release();
        release.setReleaseName(releaseName);

        // Add Release
        config.addRelease(release);

        // Save Config
        saveConfig();

        // Create new Release
        ReleaseService releaseService = new ReleaseService(releaseName);
        releaseService.saveRelease();

        // Return
        return true;
    }

    public Boolean addSiteLocal(String siteName, String releaseName) {

        // Keep only required symbols
        siteName = siteName.replaceAll(ConfigService.PATTERN, "");

        // Check if Site already exist
        if (config.getSite(siteName) != null) {
            return false;
        }

        // New Site
        Site site = new Site();
        site.setSiteName(siteName);

        // Add Site
        config.addSite(site);

        // Save Config
        saveConfig();

        // Create a new Site
        SiteService siteService = new SiteService(siteName);
        siteService.setReleaseName(releaseName);
        siteService.saveSite();

        // Return
        return true;
    }

    public Boolean addSiteRemote(String siteName, String serverURL) {

        // Keep only required symbols
        siteName = siteName.replaceAll(ConfigService.PATTERN, "");

        // Check if Site already exist
        if (config.getSite(siteName) != null) {
            return false;
        }

        // New Site
        Site site = new Site();
        site.setSiteName(siteName);
        site.setServerURL(serverURL);

        // Add Site
        config.addSite(site);

        // Save Config
        saveConfig();

        // Return
        return true;
    }

    public Boolean addUser(String userName, String password, String role,
            String email, String info) {

        // Keep only required symbols
        userName = userName.replaceAll(ConfigService.PATTERN, "");

        // Check if User already exist
        if (config.getUser(userName) != null) {
            return false;
        }

        // New User
        User user = new User();
        user.setUserName(userName);
        user.setRole(role);
        user.setEmail(email);
        user.setInfo(info);

        // Encrypt password
        String encrypted = encryptMD5(password);

        // If password encrypted
        if (encrypted != null) {
            user.setPassword(encrypted);
        }

        // Add User
        config.addUser(user);

        // Save Config
        saveConfig();

        // Return
        return true;
    }

    public String encryptMD5(String password) {

        // If password empty
        if ("".equals(password)) {
            return null;
        }

        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(password.getBytes(), 0, password.length());

            //Converts message digest value in base 16 (hex)   
            return new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.WARNING, "Security algorithm: {0}", ex);
        }

        // Return
        return null;
    }

    public Config getConfig() {

        return config;
    }

    public String getEnvName() {

        return config.getEnvName();
    }

    public String getFileName() {

        return Start.DATA_DIR + "config.json";
    }

    public Module getModule(String moduleName) {

        return config.getModule(moduleName);
    }

    public Site getSite(String siteName) {

        return config.getSite(siteName);
    }

    public List<Site> getSites() {

        return config.getSites();
    }

    public User getUser(String userName) {
        return config.getUser(userName);
    }

    public List<User> getUsers() {

        return config.getUsers();
    }

    public String getVersion() {
        return version;
    }

    public User loginUser(String userName, String password) {

        // Get User
        User user = config.getUser(userName);

        // Check if User exist
        if (user == null) {
            logger.log(Level.WARNING, "User {0} is not found.", userName);
            return null;
        }

        // Encrypt password
        String encrypted = encryptMD5(password);

        // If password encrypted and correct 
        if (encrypted != null && user.getPassword().equals(encrypted)) {
            return user;
        }

        // Error Message
        logger.log(Level.WARNING, "User {0} authentication failed.", userName);

        // Return
        return null;
    }

    public Config openConfig() {

        // If config is opened yet
        if (config != null) {
            return config;
        }

        // Read JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new File(getFileName()), Config.class);

            // Set current time and version
            Date now = new Date();
            config.setNow(now.toString());
            config.setVersion(version);

            // Return
            return config;

        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        }

        // Return
        return null;
    }

    public Config removeModule(String moduleName) {

        // Get Module
        Module module = config.getModule(moduleName);

        // If module not found
        if (module == null) {
            return null;
        }

        // Remove
        config.removeModule(module);

        // Save Configuration
        return saveConfig();
    }

    public Config removeRelease(String releaseName) {

        // Get Release
        Release release = config.getRelease(releaseName);

        // If release not found
        if (release == null) {
            return null;
        }

        // Remove
        config.removeRelease(release);

        // Save Configuration
        return saveConfig();
    }

    public Config removeSite(String siteName) {

        // Get Site
        Site site = config.getSite(siteName);

        // If Site not found
        if (site == null) {
            return null;
        }

        // Remove
        config.removeSite(site);

        // Save Configuration
        return saveConfig();
    }

    public Config removeUser(String userName) {

        // Get User
        User user = config.getUser(userName);

        // If user not found
        if (user == null) {
            return null;
        }

        // Remove
        config.removeUser(user);

        // Save Configuration
        return saveConfig();
    }

    public Config saveConfig() {

        //Create data folder if not exists
        File dir = new File(Start.DATA_DIR);
        dir.mkdir();

        // Sort hosts
        Collections.sort(config.getSites(), new Comparator<Site>() {
            @Override
            public int compare(Site s1, Site s2) {
                return s1.toString().compareToIgnoreCase(s2.toString());
            }
        });

        // Sort releases
        Collections.sort(config.getReleases(), new Comparator<Release>() {
            @Override
            public int compare(Release r1, Release r2) {
                return r1.toString().compareToIgnoreCase(r2.toString());
            }
        });

        // Sort users
        Collections.sort(config.getUsers(), new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.toString().compareToIgnoreCase(u2.toString());
            }
        });

        // Sort modules
        Collections.sort(config.getModules(), new Comparator<Module>() {
            @Override
            public int compare(Module m1, Module m2) {
                return m1.toString().compareToIgnoreCase(m2.toString());
            }
        });

        // Save JSON to File
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFileName()), config);

            // Return
            return config;

        } catch (JsonGenerationException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        } catch (JsonMappingException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Config ObjectMapper: {0}", ex);
        }

        // Return
        return null;
    }

    public Config updateConfig(String envName, String title) {

        // Create new configuration
        if (config == null) {
            config = new Config();
        }

        // Keep only required symbols
        envName = envName.replaceAll(ConfigService.PATTERN, "");

        // Update Configuration
        config.setEnvName(envName);
        config.setTitle(title);

        // Return updated configuration
        return saveConfig();
    }

    public Boolean updateModule(String moduleName, String context, String login,
            String password, String IP, Integer port) {

        // Get Module
        Module module = config.getModule(moduleName);

        // Check if Module exist
        if (module == null) {
            logger.log(Level.WARNING, "Module {0} is not found.", moduleName);
            return false;
        }

        // Encrypt password
        String encrypted = encryptBlowfish(password);

        // If password encrypted
        if (encrypted != null) {
            module.setPassword(encrypted);
        }

        // Update Module
        module.setContext(context);
        module.setLogin(login);
        module.setIP(IP);
        module.setPort(port);

        // Save Config
        saveConfig();

        // Return
        return true;
    }

    public Config updateSite(String siteName, Boolean enabled) {

        // Get Site
        Site site = config.getSite(siteName);

        // If Site not found
        if (site == null) {
            return null;
        }

        // Set Enabled
        site.setEnabled(enabled);

        // Save Configuration
        return saveConfig();
    }

    public Boolean updateUser(String userName, String password, String role,
            String email, String info) {

        // Get User
        User user = config.getUser(userName);

        // Check if User exist
        if (user == null) {
            logger.log(Level.WARNING, "User {0} is not found.", userName);
            return false;
        }

        // Encrypt password
        String encrypted = encryptMD5(password);

        // If password failed to encrypt
        if (encrypted != null) {
            user.setPassword(encrypted);
        }

        // Update User
        user.setRole(role);
        user.setEmail(email);
        user.setInfo(info);

        // Save Config
        saveConfig();

        // Return
        return true;
    }
}
