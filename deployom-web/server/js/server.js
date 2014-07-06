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
$(function() {

    $("#stopServerDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Confirm: function() {

                // AJAX
                $.ajax({
                    url: "/jersey/Server/stopServer",
                    type: "GET",
                    complete: function(data) {
                        notificationMessage("Server stopped successfully");
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#addUserDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // Check User Name
                if ($('#addUserName').val() === "") {
                    alert('Please enter User Name');
                    return false;
                }

                // Check Password
                if ($('#addUserPassword').val() === "") {
                    alert('Please enter User Password');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/addUser",
                    type: "POST",
                    data: $("#addUserForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_USERS').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#updateUserDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // Check Password
                if ($('#updateUserPassword').val() === "") {
                    alert('Please enter User Password');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/updateUser",
                    type: "POST",
                    data: $("#updateUserForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_USERS').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#removeUserDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // AJAX
                $.ajax({
                    url: "/jersey/Config/removeUser",
                    type: "POST",
                    data: $("#removeUserForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_USERS').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#updateConfigDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Update: function() {

                // Check Environment Name
                if ($('#updateConfigName').val() === "") {
                    alert('Please enter Environment Name');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/updateConfig",
                    type: "POST",
                    data: $("#updateConfigForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        location.reload();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#addSiteLocalDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Add: function() {

                // Check Site Name
                if ($('#addSiteLocalName').val() === "") {
                    alert('Please enter Site Name');
                    return false;
                }

                // Check Release Name
                if ($('#addSiteLocalReleaseName').val() === "") {
                    alert('Please enter Release Name');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/addSiteLocal",
                    type: "POST",
                    data: $("#addSiteLocalForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        alert(LANG.siteAdded);
                        $('#A_SITES').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#addSiteRemoteDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Connect: function() {

                // Empty List
                $('#addSiteRemoteName').empty();

                // Check URL
                if ($('#addSiteRemoteServerURL').val() === "") {
                    alert('Please enter Server URL');
                    return false;
                }

                // AJAX
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: $('#addSiteRemoteServerURL').val() + "/jersey/Config/getConfig",
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(config) {
                        // Show Select for Sites
                        $.each(config.site, function() {
                            var configSite = this;

                            // Skip Remove Sites
                            if (configSite.serverURL) {
                                return true;
                            }

                            // Add into List
                            $('#addSiteRemoteName').append($('<option/>').val(configSite.siteName).text(configSite.siteName));
                        });
                    }
                });
            },
            Add: function() {

                // Check Site is selected
                if (!$('#addSiteRemoteName option:selected').length) {
                    alert('Please select Site Name');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/addSiteRemote",
                    type: "POST",
                    data: $("#addSiteRemoteForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_SITES').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#addModuleDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // Check Module Name
                if ($('#addModuleName').val() === "") {
                    alert('Please enter Module Name');
                    return false;
                }

                // Check Login and Password
                if ($('#addModuleLogin').val() !== "" && $('#addModulePassword').val() === "") {
                    alert('Please enter password for Module');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/addModule",
                    type: "POST",
                    data: $("#addModuleForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_MODULES').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#updateModuleDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // Check Login and Password
                if ($('#updateModuleLogin').val() !== "" && $('#updateModulePassword').val() === "") {
                    alert('Please enter password for Module');
                    return false;
                }

                // AJAX
                $.ajax({
                    url: "/jersey/Config/updateModule",
                    type: "POST",
                    data: $("#updateModuleForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_MODULES').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });

    $("#removeModuleDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // AJAX
                $.ajax({
                    url: "/jersey/Config/removeModule",
                    type: "POST",
                    data: $("#removeModuleForm").serialize(),
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_MODULES').click();
                    }
                });

                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        position: "center"
    });
});

function afterLogin(config, role) {

    // For tabs
    var ul = $('<ul/>');

    // Add Server tab
    var serverLi = $('<li/>', {'class': 'ui-state-highlight'});
    var a = $('<a/>', {text: "Server", href: '#SERVER'});
    serverLi.append(a);
    ul.append(serverLi);
    $('#tabsDiv').append($('<div/>', {id: 'SERVER'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/Config/getConfig",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#SERVER').empty();
                $('#SERVER').prepend(LANG.loading);
            },
            error: function() {
                $('#SERVER').empty();
                $('#SERVER').prepend(LANG.submitError);
            },
            success: function(config) {
                $('#SERVER').empty();
                $('#SERVER').append(serverTab(config, role));
            }
        });
    });

    // Show on Load
    a.click();

    // Add Sites tab
    var sitesLi = $('<li/>');
    var a = $('<a/>', {id: 'A_SITES', text: "Sites", href: '#SITES'});
    sitesLi.append(a);
    ul.append(sitesLi);
    $('#tabsDiv').append($('<div/>', {id: 'SITES'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/Config/getConfig",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#SITES').empty();
                $('#SITES').prepend(LANG.loading);
            },
            error: function() {
                $('#SITES').empty();
                $('#SITES').prepend(LANG.submitError);
            },
            success: function(config) {
                $('#SITES').empty();
                $('#SITES').append(sitesTab(config, role));
            }
        });
    });

    // Add Release for Local Sites
    $.each(config.release, function() {
        var release = this;
        $('#addSiteLocalReleaseName').append($('<option/>').val(release.releaseName).text(release.releaseName));
    });

    // Add Users tab
    var usersLi = $('<li/>');
    var a = $('<a/>', {id: 'A_USERS', text: "Users", href: '#USERS'});
    usersLi.append(a);
    ul.append(usersLi);
    $('#tabsDiv').append($('<div/>', {id: 'USERS'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/Config/getConfig",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#USERS').empty();
                $('#USERS').prepend(LANG.loading);
            },
            error: function() {
                $('#USERS').empty();
                $('#USERS').prepend(LANG.submitError);
            },
            success: function(config) {
                $('#USERS').empty();
                $('#USERS').append(usersTab(config, role));
            }
        });
    });

    // Add Modules tab
    var modulesLi = $('<li/>');
    var a = $('<a/>', {id: 'A_MODULES', text: "Modules", href: '#MODULES'});
    modulesLi.append(a);
    ul.append(modulesLi);
    $('#tabsDiv').append($('<div/>', {id: 'MODULES'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/Config/getConfig",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#MODULES').empty();
                $('#MODULES').prepend(LANG.loading);
            },
            error: function() {
                $('#MODULES').empty();
                $('#MODULES').prepend(LANG.submitError);
            },
            success: function(config) {
                $('#MODULES').empty();
                $('#MODULES').append(modulesTab(config, role));
            }
        });
    });

    // Add Logging tab
    var logLi = $('<li/>');
    var a = $('<a/>', {id: 'A_LOG', text: "Logging", href: '#LOG'});
    logLi.append(a);
    ul.append(logLi);
    $('#tabsDiv').append($('<div/>', {id: 'LOG'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/Server/getLog",
            type: "GET",
            beforeSend: function(data) {
                $('#LOG').empty();
                $('#LOG').prepend(LANG.loading);
            },
            error: function() {
                $('#LOG').empty();
                $('#LOG').prepend(LANG.submitError);
            },
            success: function(log) {
                $('#LOG').empty();

                // Show Log
                var pre = $('<pre/>', {'text': log});
                $('#LOG').append(pre);
            }
        });
    });

    // Return
    return ul;
}

function serverTab(config, role) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    div.append(table);

    // Flows Row
    var td1 = $('<td/>', {'class': 'ui'});
    table.append($('<tr/>', {'class': "ui"}).append(td1));

    // Dashboard
    var dashboardButton = $('<button/>', {text: 'DASHBOARD', 'class': 'flow', 'title': 'Dashboard with Flows and Registered Events'});
    setModuleImage(dashboardButton).click(function() {
        location.replace('/dashboard');
    });
    td1.append(dashboardButton);

    // Charts
    var chartsButton = $('<button/>', {text: 'CHARTS', 'class': 'flow', 'title': 'Charts'});
    setModuleImage(chartsButton).click(function() {
        location.replace('/charts');
    });
    td1.append(chartsButton);

    // If no Sites
    if (!config.site.length) {
        dashboardButton.button('disable');
        chartsButton.button('disable');
    }

    // Designer
    var designerButton = $('<button/>', {text: 'DESIGNER', 'class': 'flow', 'title': 'Release Designer'});
    setModuleImage(designerButton).click(function() {
        location.replace('/designer');
    });
    td1.append(designerButton);

    // If no Releases
    if (!config.release.length) {
        designerButton.addClass('ui-state-highlight');
    }

    // History
    var historyButton = $('<button/>', {text: 'HISTORY', 'class': 'flow', 'title': 'Events and Operations History'});
    setModuleImage(historyButton).click(function() {
        location.replace('/history');
    });
    td1.append(historyButton);

    // Help
    var helpButton = $('<button/>', {text: 'HELP', 'class': 'flow', 'title': 'Online Help'});
    setModuleImage(helpButton).click(function() {
        location.replace('/help');
    });
    td1.append(helpButton);

    // Logout
    var logoutButton = $('<button/>', {text: 'LOGOUT', 'class': 'flow', 'title': 'Logout'});
    setModuleImage(logoutButton).click(function() {
        // Remove cookie
        $.removeCookie("userName", {path: '/'});
        $.removeCookie("password", {path: '/'});

        // Reload
        location.reload();
    });
    td1.append(logoutButton);

    if (role !== "admin") {
        designerButton.button("disable");
    }

    // Create a table
    var sitesTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    div.append(sitesTable);

    // Header Row
    var headerTd1 = $('<td/>', {'class': 'ui center', text: 'Site Management'});
    sitesTable.append($('<tr/>', {'class': 'ui-widget-header'}).append(headerTd1));

    // Sites Row
    var sitesTd1 = $('<td/>', {'class': 'ui'});
    sitesTable.append($('<tr/>', {'class': "ui"}).append(sitesTd1));

    // If no Sites
    if (!config.site.length) {
        sitesTd1.append(LANG.noSites);
    }

    $.each(config.site, function() {
        var site = this;

        // Site Button
        var siteButton = $('<button/>', {text: site.siteName, 'class': 'flow'});
        sitesTd1.append(siteButton);

        // If updating disabled
        if (!site.enabled) {
            siteButton.text(siteButton.text() + ' [DISABLED]');
        }

        // On Click
        setSiteImage(siteButton).click(function() {
            location.replace('/site#' + site.siteName);
        });

        var siteUrl = "/jersey/Site/getSite";
        if (site.serverURL) {
            siteUrl = site.serverURL + siteUrl;
        }

        // Check site
        $.ajax({
            url: siteUrl,
            type: "POST",
            data: {SiteName: site.siteName},
            dataType: "json",
            xhrFields: {
                withCredentials: true
            },
            success: function(site) {
                siteButton.addClass('ui-state-ok');
            },
            error: function(jqXHR) {
                siteButton.addClass('ui-state-error');

                if (jqXHR.status === 0) {
                    notificationMessage(site.siteName + ": " + LANG.connectionError);
                } else if (jqXHR.status === 401) {
                    notificationMessage(site.siteName + ": " + LANG.authError);
                } else {
                    notificationMessage(site.siteName + ": " + LANG.submitError);
                }
            }
        });
    });

    // Create a table
    var modulesTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    div.append(modulesTable);

    // Header Row
    var headerTd1 = $('<td/>', {'class': 'ui center', text: 'Modules'});
    modulesTable.append($('<tr/>', {'class': 'ui-widget-header'}).append(headerTd1));

    // Modules Row
    var modulesTd1 = $('<td/>', {'class': 'ui'});
    modulesTable.append($('<tr/>', {'class': "ui"}).append(modulesTd1));

    var anyModule = false;

    $.each(config.module, function() {
        var module = this;

        // Skip modules w/o context
        if (!module.context) {
            return true;
        }

        // Module
        var moduleButton = $('<button/>', {text: module.moduleName.toUpperCase(), 'class': 'flow'});
        setModuleImage(moduleButton).click(function() {
            location.replace(module.context);
        });
        modulesTd1.append(moduleButton);
        anyModule = true;
    });

    // If no modules
    if (!config.module.length || !anyModule) {
        modulesTd1.append(LANG.noModules);
    }

    // Add Buttons
    var updateConfigButton = $('<button/>', {text: "Update Environment"});
    setButtonIcon(updateConfigButton).click(function() {
        $('#updateConfigName').val(config.envName);
        $('#updateConfigTitle').val(config.title);
        $("#updateConfigDialog").dialog("open");
    });

    var stopServerButton = $('<button/>', {text: "Stop Server"});
    setButtonIcon(stopServerButton).click(function() {
        $("#stopServerDialog").dialog("open");
    });

    if (role !== "admin") {
        stopServerButton.button("disable");
    }

    return div.append(updateConfigButton, stopServerButton);
}

function sitesTab(config, role) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Site'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Type'});
    var td3 = $('<td/>', {'class': 'ui hostname', text: 'Hosts'});
    var td4 = $('<td/>', {'class': 'ui hostname', text: 'Connection'});
    var td5 = $('<td/>', {'class': 'ui hostname', text: 'Release'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(table);

    // If no releases or sites defined
    if (!config.release.length) {
        var td1 = $('<td/>', {'colspan': 6}).append(LANG.noReleases);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    } else if (!config.site.length) {
        var td1 = $('<td/>', {'colspan': 6}).append(LANG.noSites);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // If releases defined and Admin
    if (config.release.length) {

        // Add Buttons
        var addSiteLocalButton = $('<button/>', {text: "Add Local Site"});
        setButtonIcon(addSiteLocalButton).click(function() {
            $("#addSiteLocalDialog").dialog("open");

            // Set OnChange
            $("#addSiteLocalReleaseName").change(function() {

                // Clear List
                $('#addSiteLocalHostType').empty();

                // If Release Name not found
                if ($(this).val() === "") {
                    alert('Please choose Release Name');
                    return false;
                }

                // Open Release
                $.ajax({
                    url: "/jersey/Release/getHosts",
                    type: "POST",
                    data: {ReleaseName: $(this).val()},
                    dataType: "json",
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(release) {
                        // For each Host
                        $.each(release.host, function() {
                            var host = this;

                            $('#addSiteLocalHostType').append($('<option/>').val(host.hostType).text(host.hostType));
                        });
                    }
                });
            });

            // Change
            $("#addSiteLocalReleaseName").change();
        });

        var addSiteRemoteButton = $('<button/>', {text: "Add Remote Site"});
        setButtonIcon(addSiteRemoteButton).click(function() {
            $("#addSiteRemoteDialog").dialog("open");
        });
        div.append(addSiteLocalButton, addSiteRemoteButton);

        if (role !== "admin") {
            addSiteLocalButton.button("disable");
            addSiteRemoteButton.button("disable");
        }
    }

    // For each site
    $.each(config.site, function() {
        var site = this;
        var siteName = site.siteName;

        var td1 = $('<td/>', {'class': 'ui hostname'});
        var td2 = $('<td/>', {'class': 'ui '});
        var td3 = $('<td/>', {'class': 'ui center'});
        var td4 = $('<td/>', {'class': 'ui center'});
        var td5 = $('<td/>', {'class': 'ui center'});
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);

        // Check site Local or Remote
        td2.text('Local');
        if (site.serverURL) {
            td2.text('Remote, ');
            var a = $('<a/>', {text: site.serverURL, target: '_blank', href: site.serverURL});
            td2.append(a);
        }

        // Add Site Button
        var siteButton = $('<button/>', {"class": "hostname", text: siteName});

        if (!site.enabled) {
            td2.append(', ' + LANG.siteDisabled);
            siteButton.text(siteButton.text() + ' [DISABLED]');
        }

        // Set Site Icon
        setSiteIcon(siteButton).click(function() {

            // Create Site Menu
            var siteMenu = [];

            // Only for Local Sites
            if (!site.serverURL) {
                // Update site
                var updateSiteA = $('<a/>', {text: "Disable Site"});
                updateSiteA.prepend($('<img/>', {'class': 'menu', src: '/server/img/other.png'}));
                if (!site.enabled) {
                    updateSiteA.text("Enable Site");
                    updateSiteA.prepend($('<img/>', {'class': 'menu', src: '/server/img/start.png'}));
                }

                // Set Onclick
                updateSiteA.click(function() {
                    $('#menu').hide();
                    // AJAX
                    $.ajax({
                        url: "/jersey/Config/updateSite",
                        type: "POST",
                        data: {SiteName: siteName, Enabled: !site.enabled},
                        error: function() {
                            notificationMessage(LANG.submitError);
                        },
                        complete: function(data) {
                            $('#A_SITES').click();
                        }
                    });
                });

                siteMenu.push($('<li/>').append(updateSiteA));
            }

            $('#menu').empty();
            $('#menu').append(siteMenu);
            $('#menu').menu("refresh");
            $('#menu').show().position({
                my: "left top",
                at: "left bottom",
                of: this
            });
            return false;
        });
        td1.append(siteButton);

        // Disable button for non Admin
        if (role !== "admin") {
            siteButton.button("disable");
        }

        var siteUrl = "/jersey/Site/getSite";
        if (site.serverURL) {
            siteUrl = site.serverURL + siteUrl;
        }

        // Check site
        $.ajax({
            url: siteUrl,
            type: "POST",
            data: {SiteName: siteName},
            dataType: "json",
            xhrFields: {
                withCredentials: true
            },
            beforeSend: function(data) {
                td4.text(LANG.loading);
            },
            success: function(site) {
                td4.text(LANG.ok);
                var img = $('<img/>', {src: '/server/img/ok.png', 'class': 'index'});
                td4.prepend(img);
                td3.append(site.host.length);
                td5.text(site.releaseName);

                // Ok
                siteButton.addClass('ui-state-ok');
            },
            error: function() {
                td4.text(LANG.error);
                var img = $('<img/>', {src: '/server/img/error.png', 'class': 'index'});
                td4.prepend(img);
                tr.addClass('ui-state-error');

                // Error
                siteButton.addClass('ui-state-error');
            }
        });

        // Add Site
        table.append(tr);
    });

    // Return table
    return div;
}

function usersTab(config, role) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'User Name'});
    var td2 = $('<td/>', {'class': 'ui hostname', text: 'Role'});
    var td3 = $('<td/>', {'class': 'ui hostname', text: 'Email'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Information'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4));

    // If no users defined
    if (!config.user.length) {
        var td1 = $('<td/>', {'colspan': 2}).append(LANG.noUsers);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each user
    $.each(config.user, function() {
        var user = this;
        var userName = user.userName;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui hostname'});
        var td2 = $('<td/>', {'class': 'ui', text: user.role || ''});
        var td3 = $('<td/>', {'class': 'ui', text: user.email || ''});
        var td4 = $('<td/>', {'class': 'ui', text: user.info || ''});

        // User row
        table.append($('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4));

        // Add User Button
        var userButton = $('<button/>', {"class": "hostname", text: userName});

        // Set User Icon
        setUserIcon(user.role, userButton).click(function() {

            // Create User Menu
            var userMenu = [];

            // Update User
            var updateUser = $('<a/>', {text: "Update User"}).click(function() {
                $('#menu').hide();
                $("#updateUserName").val(userName);
                $("#updateUserRole").val(user.role);
                $("#updateUserEmail").val(user.email);
                $("#updateUserInfo").val(user.info);
                $("#updateUserDialog").dialog("open");
            });
            updateUser.prepend($('<img/>', {'class': 'menu', src: '/server/img/change.png'}));
            userMenu.push($('<li/>').append(updateUser));

            // Remove User
            var removeUser = $('<a/>', {text: "Remove User"}).click(function() {
                $('#menu').hide();
                $("#removeUserName").val(userName);
                $("#removeUserDialog").dialog("open");
            });
            removeUser.prepend($('<img/>', {'class': 'menu', src: '/server/img/alert.png'}));
            userMenu.push($('<li/>').append(removeUser));

            $('#menu').empty();
            $('#menu').append(userMenu);
            $('#menu').menu("refresh");
            $('#menu').show().position({
                my: "left top",
                at: "left bottom",
                of: this
            });
            return false;
        });
        td1.append(userButton);

        // Disable for Non Admin
        if (role !== "admin") {
            userButton.button("disable");
        }
    });

    // Add Buttons
    var addUserButton = $('<button/>', {text: "Add User"});
    setButtonIcon(addUserButton).click(function() {
        $("#addUserDialog").dialog("open");
    });

    // Disable for non Admin
    if (role !== "admin") {
        addUserButton.button("disable");
    }

    // Return table
    return div.append(table, addUserButton);
}

function modulesTab(config, role) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Module Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Context'});
    var td3 = $('<td/>', {'class': 'ui hostname', text: 'Login'});
    var td4 = $('<td/>', {'class': 'ui hostname', text: 'Password (encrypted)'});
    var td5 = $('<td/>', {'class': 'ui hostname', text: 'IP'});
    var td6 = $('<td/>', {'class': 'ui hostname', text: 'Port'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6));

    // If no modules defined
    if (!config.module.length) {
        var td1 = $('<td/>', {'colspan': 2}).append(LANG.noModules);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each module
    $.each(config.module, function() {
        var module = this;
        var moduleName = module.moduleName;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui hostname'});
        var td2 = $('<td/>', {'class': 'ui', text: module.context || ''});
        var td3 = $('<td/>', {'class': 'ui', text: module.login || ''});
        var td4 = $('<td/>', {'class': 'ui', text: module.password || ''});
        var td5 = $('<td/>', {'class': 'ui', text: module.ip || ''});
        var td6 = $('<td/>', {'class': 'ui', text: module.port || ''});

        // Modules row
        table.append($('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5, td6));

        // Add Module Button
        var moduleButton = $('<button/>', {"class": "hostname", text: moduleName});

        // Set Icon
        setServiceIcon(moduleButton, 'ui-icon-triangle-1-s').click(function() {

            // Create User Menu
            var moduleMenu = [];

            // Update Module
            var updateModule = $('<a/>', {text: "Update Module"}).click(function() {
                $('#menu').hide();
                $("#updateModuleName").val(moduleName);
                $("#updateModuleContext").val(module.context);
                $("#updateModuleLogin").val(module.login);
                $("#updateModuleIP").val(module.ip);
                $("#updateModulePort").val(module.port);
                $("#updateModuleDialog").dialog("open");
            });
            updateModule.prepend($('<img/>', {'class': 'menu', src: '/server/img/change.png'}));
            moduleMenu.push($('<li/>').append(updateModule));

            // Remove Module
            var removeModule = $('<a/>', {text: "Remove Module"}).click(function() {
                $('#menu').hide();
                $("#removeModuleName").val(moduleName);
                $("#removeModuleDialog").dialog("open");
            });
            removeModule.prepend($('<img/>', {'class': 'menu', src: '/server/img/alert.png'}));
            moduleMenu.push($('<li/>').append(removeModule));

            $('#menu').empty();
            $('#menu').append(moduleMenu);
            $('#menu').menu("refresh");
            $('#menu').show().position({
                my: "left top",
                at: "left bottom",
                of: this
            });
            return false;
        });
        td1.append(moduleButton);

        // Disable for non Admin
        if (role !== "admin") {
            moduleButton.button("disable");
        }
    });

    // Add Buttons
    var addModuleButton = $('<button/>', {text: "Add Module"});
    setButtonIcon(addModuleButton).click(function() {
        $("#addModuleDialog").dialog("open");
    });

    // Disable for non Admin
    if (role !== "admin") {
        addModuleButton.button("disable");
    }

    // Return table
    return div.append(table, addModuleButton);
}
