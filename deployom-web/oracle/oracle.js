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
function afterLogin(config, role) {

    // Set oracle
    $('#oracle').button({icons: {primary: "deployom-database"}}).click(function(event) {
        location.replace('/oracle');
    });

    var div = $('<div/>');

    // If no sites
    if (!config.site.length) {
        div.text(LANG.noSites);
        return div;
    }

    // Site Tabs
    var ul = $('<ul/>');

    $.each(config.site, function() {
        var configSite = this;
        var siteName = configSite.siteName;

        // Add Site tab
        var siteLi = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName, text: siteName, href: '#' + siteName, title: 'Click to refresh'});
        siteLi.append(a);
        ul.append(siteLi);

        // If updating disabled
        if (!configSite.enabled) {
            a.append(' [DISABLED]');
        }

        $("#tabsDiv").append($('<div/>', {id: configSite.siteName}));

        a.click(function() {
            var url = "/jersey/Site/getModules";
            if (configSite.serverURL) {
                url = configSite.serverURL + url;
            }

            // Request Services
            $.ajax({
                url: url,
                type: "POST",
                data: {SiteName: siteName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + configSite.siteName).empty();
                    $('#' + configSite.siteName).prepend(LANG.loading);
                },
                success: function(site) {
                    $('#' + configSite.siteName).empty();
                    site.serverURL = configSite.serverURL;
                    $('#' + configSite.siteName).prepend(oracleTab(site, ul));
                },
                error: function() {
                    $('#' + configSite.siteName).empty();
                    $('#' + configSite.siteName).prepend(LANG.submitError);
                }
            });
        });
    });

    // Return
    return ul;
}

function oracleTab(site, ul) {

    var div = $('<div/>');

    // Site Header
    var h1 = $('<h1/>', {'class': "center", text: 'Oracle Services on ' + site.siteName});
    div.append(h1);

    // Check if no hosts
    if (!site.host.length) {
        div.text(LANG.noHosts);
        return div;
    }

    // For each Host
    $.each(site.host, function() {
        var host = this;

        // Create a table
        var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
        var td = $('<td/>', {'class': 'ui hostname', colspan: 2, text: host.hostName});
        table.append($('<tr/>', {'class': "ui-widget-header"}).append(td));

        var anyModule = false;

        // For each service
        $.each(host.service, function() {
            var service = this;

            // For each Module
            $.each(service.module, function() {
                var module = this;

                // Skip other modules
                if (!module.moduleName || module.moduleName !== 'oracle') {
                    return true;
                }

                var td1 = $('<td/>', {'class': 'ui hostname', text: service.serviceName});
                var td2 = $('<td/>', {'class': 'ui'});
                table.append($('<tr/>').append(td1, td2));

                // Sessions
                var sessionsButton = $('<button/>', {"class": "flowService", text: 'Active Sessions'});
                setServiceIcon(sessionsButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Active Sessions [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Oracle/getSessions";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request Servers
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName,
                            HostName: host.hostName, ServiceName: service.serviceName, ModuleName: module.moduleName},
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        beforeSend: function(data) {
                            dialog.empty();
                            dialog.prepend(LANG.loading);
                        },
                        success: function(database) {
                            dialog.empty();
                            dialog.prepend(sessionsDiv(database));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(sessionsButton);

                // Long Run
                var longRunButton = $('<button/>', {"class": "flowService", text: 'Long Running'});
                setServiceIcon(longRunButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Long Running [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Oracle/getLongRunning";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request Servers
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName,
                            HostName: host.hostName, ServiceName: service.serviceName, ModuleName: module.moduleName},
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        beforeSend: function(data) {
                            dialog.empty();
                            dialog.prepend(LANG.loading);
                        },
                        success: function(database) {
                            dialog.empty();
                            dialog.prepend(longRunningDiv(database));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(longRunButton);

                // Analyzed
                var analyzedButton = $('<button/>', {"class": "flowService", text: 'Last Analyzed'});
                setServiceIcon(analyzedButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Last Analyzed [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Oracle/getLastAnalyzed";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request Servers
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName,
                            HostName: host.hostName, ServiceName: service.serviceName, ModuleName: module.moduleName},
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        beforeSend: function(data) {
                            dialog.empty();
                            dialog.prepend(LANG.loading);
                        },
                        success: function(database) {
                            dialog.empty();
                            dialog.prepend(lastAnalyzedDiv(database));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(analyzedButton);

                // Data Files
                var dataFilesButton = $('<button/>', {"class": "flowService", text: 'Data Files'});
                setServiceIcon(dataFilesButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Data Files [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Oracle/getDataFiles";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request Servers
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName,
                            HostName: host.hostName, ServiceName: service.serviceName, ModuleName: module.moduleName},
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        beforeSend: function(data) {
                            dialog.empty();
                            dialog.prepend(LANG.loading);
                        },
                        success: function(database) {
                            dialog.empty();
                            dialog.prepend(datafilesDiv(database));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(dataFilesButton);

                // Tablespaces
                var tablespacesButton = $('<button/>', {"class": "flowService", text: 'Tablespace Usage'});
                setServiceIcon(tablespacesButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Tablespace Usage [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Oracle/getTablespaces";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request Servers
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName,
                            HostName: host.hostName, ServiceName: service.serviceName, ModuleName: module.moduleName},
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        beforeSend: function(data) {
                            dialog.empty();
                            dialog.prepend(LANG.loading);
                        },
                        success: function(database) {
                            dialog.empty();
                            dialog.prepend(tablespacesDiv(site, host, service, module, database));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(tablespacesButton);

                // Add Class
                if (!service.online) {
                    analyzedButton.addClass('ui-state-error');
                    sessionsButton.addClass('ui-state-error');
                    dataFilesButton.addClass('ui-state-error');
                    longRunButton.addClass('ui-state-error');
                    tablespacesButton.addClass('ui-state-error');
                }

                anyModule = true;
            });
        });

        // If modules found
        if (anyModule) {
            div.append(table);
        }
    });

    // Return Div
    return div;
}

function sessionsDiv(database) {
    var div = $('<div/>');

    // Create a Database table
    var databaseTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Database Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Log Mode'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Mode'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Database Role'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Platform Name'});
    databaseTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(databaseTable);

    // Create a Limits table
    var h2 = $('<h2/>', {'class': "center", text: 'Limits'});
    var limitsTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Resource Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Current Utilization'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Max Utilization'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Initial Allocation'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Limit'});
    limitsTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(h2, limitsTable);

    // Create a Sessions table
    var h2 = $('<h2/>', {'class': "center", text: 'Sessions'});
    var sessionsTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui center', text: 'Count'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Status'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'OS User'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Machine'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Program'});
    sessionsTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(h2, sessionsTable);

    // If no database
    if (!database || !database.databaseName) {
        var td1 = $('<td/>', {'colspan': 5}).append("Database is not reachable");
        databaseTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return databaseTable;
    }

    // Data columns
    var td1 = $('<td/>', {'class': 'ui', text: database.databaseName});
    var td2 = $('<td/>', {'class': 'ui', text: database.logMode});
    var td3 = $('<td/>', {'class': 'ui', text: database.openMode});
    var td4 = $('<td/>', {'class': 'ui', text: database.databaseRole});
    var td5 = $('<td/>', {'class': 'ui', text: database.platformName});

    // Add row
    var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
    databaseTable.append(tr);

    // For each limit
    $.each(database.limit, function() {
        var limit = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: limit.resourceName});
        var td2 = $('<td/>', {'class': 'ui', text: limit.current});
        var td3 = $('<td/>', {'class': 'ui', text: limit.max});
        var td4 = $('<td/>', {'class': 'ui', text: limit.initial});
        var td5 = $('<td/>', {'class': 'ui', text: limit.limit});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
        limitsTable.append(tr);

        // Error
        if (limit.max === limit.limit) {
            tr.addClass('ui-state-error');
        }
    });

    // For each session
    $.each(database.session, function() {
        var session = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: session.count});
        var td2 = $('<td/>', {'class': 'ui', text: session.status});
        var td3 = $('<td/>', {'class': 'ui', text: session.user || ''});
        var td4 = $('<td/>', {'class': 'ui', text: session.machine});
        var td5 = $('<td/>', {'class': 'ui', text: session.program || ''});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
        sessionsTable.append(tr);

        // Highlight
        if (session.status === 'ACTIVE') {
            tr.addClass('ui-state-highlight');
        }
    });

    // Return table
    return div;
}

function longRunningDiv(database) {
    var div = $('<div/>');

    // Create a Database table
    var databaseTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Database Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Log Mode'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Mode'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Database Role'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Platform Name'});
    databaseTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(databaseTable);

    // Create a Sessions table
    var h2 = $('<h2/>', {'class': "center", text: 'Long Running'});
    var sessionsTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui center', text: 'Sid'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Machine'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Message'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Elapsed, sec'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Remaining, sec'});
    var td6 = $('<td/>', {'class': 'ui center', text: 'Progress, %'});
    sessionsTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6));
    div.append(h2, sessionsTable);

    // If no database
    if (!database || !database.databaseName) {
        var td1 = $('<td/>', {'colspan': 5}).append("Database is not reachable");
        databaseTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return databaseTable;
    }

    // Data columns
    var td1 = $('<td/>', {'class': 'ui', text: database.databaseName});
    var td2 = $('<td/>', {'class': 'ui', text: database.logMode});
    var td3 = $('<td/>', {'class': 'ui', text: database.openMode});
    var td4 = $('<td/>', {'class': 'ui', text: database.databaseRole});
    var td5 = $('<td/>', {'class': 'ui', text: database.platformName});

    // Add row
    var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
    databaseTable.append(tr);

    // If no sessions
    if (!database.session.length) {
        var td1 = $('<td/>', {'colspan': 6}).append("Long running queries are not found");
        sessionsTable.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each session
    $.each(database.session, function() {
        var session = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: session.sid});
        var td2 = $('<td/>', {'class': 'ui', text: session.machine});
        var td3 = $('<td/>', {'class': 'ui', text: session.message});
        var td4 = $('<td/>', {'class': 'ui', text: session.elapsedSeconds});
        var td5 = $('<td/>', {'class': 'ui', text: session.remainingSeconds});
        var td6 = $('<td/>', {'class': 'ui', text: session.progressPercent});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5, td6);
        sessionsTable.append(tr);

        // Highlight
        if (session.remainingSeconds > 120) {
            tr.addClass('ui-state-highlight');
        }
    });

    // Return table
    return div;
}

function lastAnalyzedDiv(database) {
    var div = $('<div/>');

    // Create a Database table
    var databaseTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Database Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Log Mode'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Mode'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Database Role'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Platform Name'});
    databaseTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(databaseTable);

    // Create a Analyzed table
    var h2 = $('<h2/>', {'class': "center", text: 'Last Analyzed'});
    var analyzedTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Last Analyzed'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Count'});
    analyzedTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2));
    div.append(h2, analyzedTable);

    // If no database
    if (!database || !database.databaseName) {
        var td1 = $('<td/>', {'colspan': 5}).append("Database is not reachable");
        databaseTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return databaseTable;
    }

    // Data columns
    var td1 = $('<td/>', {'class': 'ui', text: database.databaseName});
    var td2 = $('<td/>', {'class': 'ui', text: database.logMode});
    var td3 = $('<td/>', {'class': 'ui', text: database.openMode});
    var td4 = $('<td/>', {'class': 'ui', text: database.databaseRole});
    var td5 = $('<td/>', {'class': 'ui', text: database.platformName});

    // Add row
    var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
    databaseTable.append(tr);

    // For each count
    $.each(database.count, function() {
        var count = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: count.lastAnalyzed});
        var td2 = $('<td/>', {'class': 'ui', text: count.count});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2);
        analyzedTable.append(tr);

        // Error
        if (count.lastAnalyzed === "NO STATS") {
            tr.addClass('ui-state-highlight');
        }
    });

    // Return table
    return div;
}

function tablespacesDiv(site, host, service, module, database) {
    var div = $('<div/>');

    // Create a Database table
    var databaseTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Database Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Log Mode'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Mode'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Database Role'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Platform Name'});
    databaseTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(databaseTable);

    // Create a Tablespace table
    var h2 = $('<h2/>', {'class': "center", text: 'Tablespaces'});
    var tablespacesTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Tablespace Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Current, Mb'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Max, Mb'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Used, Mb'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Used, %'});
    tablespacesTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(h2, tablespacesTable);

    // If no database
    if (!database || !database.databaseName) {
        var td1 = $('<td/>', {'colspan': 5}).append("Database is not reachable");
        databaseTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return databaseTable;
    }

    // Data columns
    var td1 = $('<td/>', {'class': 'ui', text: database.databaseName});
    var td2 = $('<td/>', {'class': 'ui', text: database.logMode});
    var td3 = $('<td/>', {'class': 'ui', text: database.openMode});
    var td4 = $('<td/>', {'class': 'ui', text: database.databaseRole});
    var td5 = $('<td/>', {'class': 'ui', text: database.platformName});

    // Add row
    var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
    databaseTable.append(tr);

    // For each tablespace
    $.each(database.tablespace, function() {
        var tablespace = this;

        // Create Tablespace button
        var tablespaceButton = $('<button/>', {"class": "hostname", text: tablespace.tablespaceName});
        setServiceIcon(tablespaceButton, 'ui-icon-extlink').click(function() {

            // Set Div
            var dialog = $("<div/>");

            // Create Dialog
            dialog.dialog({
                autoOpen: true,
                modal: true,
                closeOnEscape: true,
                height: 620,
                width: 830,
                title: 'Tablespace ' + tablespace.tablespaceName + ' segments',
                buttons: {
                    Close: function() {
                        $(this).dialog("close");
                    }
                }
            });

            var url = "/jersey/Oracle/getSegments";
            if (site.serverURL) {
                url = site.serverURL + url;
            }

            // Request Servers
            $.ajax({
                url: url,
                type: "POST",
                data: {SiteName: site.siteName, HostName: host.hostName, ServiceName: service.serviceName,
                    ModuleName: module.moduleName, TablespaceName: tablespace.tablespaceName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    dialog.empty();
                    dialog.prepend(LANG.loading);
                },
                success: function(tablespace) {
                    dialog.empty();
                    var segmentsTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
                    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Segment Name'});
                    var td2 = $('<td/>', {'class': 'ui center', text: 'Partition Name'});
                    var td3 = $('<td/>', {'class': 'ui center', text: 'Segment Type'});
                    var td4 = $('<td/>', {'class': 'ui center', text: 'Table Name'});
                    var td5 = $('<td/>', {'class': 'ui center', text: 'Size, Mb'});
                    segmentsTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
                    dialog.append(segmentsTable);

                    // For each segment
                    $.each(tablespace.segment, function() {
                        var segment = this;

                        // Data columns
                        var td1 = $('<td/>', {'class': 'ui', text: segment.segmentName});
                        var td2 = $('<td/>', {'class': 'ui', text: segment.partitionName || ''});
                        var td3 = $('<td/>', {'class': 'ui', text: segment.segmentType});
                        var td4 = $('<td/>', {'class': 'ui', text: segment.tableName || ''});
                        var td5 = $('<td/>', {'class': 'ui', text: segment.sizeMb});

                        // Add row
                        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
                        segmentsTable.append(tr);
                    });
                },
                error: function() {
                    notificationMessage(LANG.submitError);
                }
            });
        });

        // Data columns
        var td1 = $('<td/>', {'class': 'ui'}).append(tablespaceButton);
        var td2 = $('<td/>', {'class': 'ui', text: tablespace.currentMb});
        var td3 = $('<td/>', {'class': 'ui', text: tablespace.maxMb});
        var td4 = $('<td/>', {'class': 'ui', text: tablespace.usedMb});
        var td5 = $('<td/>', {'class': 'ui', text: tablespace.usedPercent + '%'});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
        tablespacesTable.append(tr);

        // Error
        if (tablespace.usedPercent > 80) {
            tr.addClass('ui-state-error');
        }
    });

    // Return table
    return div;
}

function datafilesDiv(database) {
    var div = $('<div/>');

    // Create a Database table
    var databaseTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Database Name'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Log Mode'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Mode'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Database Role'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Platform Name'});
    databaseTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(databaseTable);

    // Create a Tablespace table
    var h2 = $('<h2/>', {'class': "center", text: 'Data Files'});
    var datafilesTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Tablespace Name'});
    var td2 = $('<td/>', {'class': 'ui hostname', text: 'File Name'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Current, Mb'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Max, Mb'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Autoextensible'});
    var td6 = $('<td/>', {'class': 'ui center', text: 'Status'});
    datafilesTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6));
    div.append(h2, datafilesTable);

    // If no database
    if (!database || !database.databaseName) {
        var td1 = $('<td/>', {'colspan': 5}).append("Database is not reachable");
        databaseTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return databaseTable;
    }

    // Data columns
    var td1 = $('<td/>', {'class': 'ui', text: database.databaseName});
    var td2 = $('<td/>', {'class': 'ui', text: database.logMode});
    var td3 = $('<td/>', {'class': 'ui', text: database.openMode});
    var td4 = $('<td/>', {'class': 'ui', text: database.databaseRole});
    var td5 = $('<td/>', {'class': 'ui', text: database.platformName});

    // Add row
    var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
    databaseTable.append(tr);

    // For each tablespace
    $.each(database.tablespace, function() {
        var tablespace = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: tablespace.tablespaceName});
        var td2 = $('<td/>', {'class': 'ui', text: tablespace.fileName});
        var td3 = $('<td/>', {'class': 'ui', text: tablespace.currentMb});
        var td4 = $('<td/>', {'class': 'ui', text: tablespace.maxMb});
        var td5 = $('<td/>', {'class': 'ui', text: tablespace.autoextensible});
        var td6 = $('<td/>', {'class': 'ui', text: tablespace.status});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5, td6);
        datafilesTable.append(tr);
    });

    // Return table
    return div;
}
