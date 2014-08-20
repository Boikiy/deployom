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

    // Set weblogic
    $('#weblogic').button({icons: {primary: "deployom-web"}}).click(function(event) {
        location.replace('/weblogic');
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
                    $('#' + configSite.siteName).prepend(weblogicTab(site, ul));
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

function weblogicTab(site, ul) {

    var div = $('<div/>');

    // Site Header
    var h1 = $('<h1/>', {'class': "center", text: 'Weblogic Services on ' + site.siteName});
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
                if (!module.moduleName || module.moduleName !== 'weblogic') {
                    return true;
                }

                var td1 = $('<td/>', {'class': 'ui hostname', text: service.serviceName});
                var td2 = $('<td/>', {'class': 'ui'});
                table.append($('<tr/>').append(td1, td2));

                // Servers
                var serversButton = $('<button/>', {"class": "flowService", text: 'Servers Information'});
                setServiceIcon(serversButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Servers Information [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Weblogic/getServers";
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
                        success: function(servers) {
                            dialog.empty();
                            dialog.prepend(serversDiv(servers));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(serversButton);

                // JDBC
                var jdbcButton = $('<button/>', {"class": "flowService", text: 'JDBC Data Sources'});
                setServiceIcon(jdbcButton, 'ui-icon-extlink').click(function() {

                    // Set Div
                    var dialog = $("<div/>");

                    // Create Dialog
                    dialog.dialog({
                        autoOpen: true,
                        modal: true,
                        closeOnEscape: true,
                        height: 620,
                        width: 830,
                        title: 'Servers Information [' + service.serviceName + ']',
                        buttons: {
                            Close: function() {
                                $(this).dialog("close");
                            }
                        }
                    });

                    var url = "/jersey/Weblogic/getJDBC";
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // Request JDBC
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
                        success: function(servers) {
                            dialog.empty();
                            dialog.prepend(jdbcDiv(servers));
                        },
                        error: function() {
                            dialog.empty();
                            dialog.prepend(LANG.submitError);
                        }
                    });
                });
                td2.append(jdbcButton);

                // Add Class
                if (!service.online) {
                    serversButton.addClass('ui-state-error');
                    jdbcButton.addClass('ui-state-error');
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

function serversDiv(servers) {
    var div = $('<div/>');

    // Create a Server table
    var serverTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Server'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'State'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Uptime'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Open Sockets', title: 'The current number sockets on the server that are open and receiving requests'});
    serverTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4));
    div.append(serverTable);

    // Create a Heap table
    var h2 = $('<h2/>', {'class': "center", text: 'Heap'});
    var heapTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Server'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Free', title: 'The current amount of free memory, in Mb, that is in the WebLogic server Java Virtual Machine (JVM) heap'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Current'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Max'});
    heapTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4));
    div.append(h2, heapTable);

    // Create a Thread table
    var h2 = $('<h2/>', {'class': "center", text: 'Threads'});
    var threadTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Server'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Idle', title: 'The number of threads in the server execution queue that are idle or which are not being used to process data'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Total'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Hogging', title: 'These threads will either be declared as stuck after the configured timeout or will return to the pool before that'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Queue', title: 'The number of pending requests in the priority queue'});
    var td6 = $('<td/>', {'class': 'ui center', text: 'Throughput', title: 'The mean number of requests completed per second'});
    threadTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6));
    div.append(h2, threadTable);

    // Create a JTA table
    var h2 = $('<h2/>', {'class': "center", text: 'Transactions'});
    var jtaTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Server'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'Active'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Total'});
    var td4 = $('<td/>', {'class': 'ui center', text: 'Commited'});
    var td5 = $('<td/>', {'class': 'ui center', text: 'Rolledback'});
    jtaTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));
    div.append(h2, jtaTable);

    // If no servers defined
    if (!servers || !servers.length) {
        var td1 = $('<td/>', {'colspan': 5}).append("No Servers found");
        serverTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return div;
    }

    // For each server
    $.each(servers, function() {
        var server = this;

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: server.serverName});
        var td2 = $('<td/>', {'class': 'ui', text: server.state});
        var td3 = $('<td/>', {'class': 'ui', text: server.uptime});
        var td4 = $('<td/>', {'class': 'ui', text: server.openSocket});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4);
        serverTable.append(tr);

        // Error
        if (!server.state.match(/RUNNING/i)) {
            tr.addClass('ui-state-error');
        }

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: server.serverName});
        var td2 = $('<td/>', {'class': 'ui', text: server.heapFree});
        var td3 = $('<td/>', {'class': 'ui', text: server.heapCurrent});
        var td4 = $('<td/>', {'class': 'ui', text: server.heapMax});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4);
        heapTable.append(tr);

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: server.serverName});
        var td2 = $('<td/>', {'class': 'ui', text: server.threadIdle});
        var td3 = $('<td/>', {'class': 'ui', text: server.threadTotal});
        var td4 = $('<td/>', {'class': 'ui', text: server.threadHogging});
        var td5 = $('<td/>', {'class': 'ui', text: server.threadQueue});
        var td6 = $('<td/>', {'class': 'ui', text: server.throughput});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5, td6);
        threadTable.append(tr);

        // Error
        if (server.threadHogging > 0) {
            tr.addClass('ui-state-error');
        }

        // Data columns
        var td1 = $('<td/>', {'class': 'ui', text: server.serverName});
        var td2 = $('<td/>', {'class': 'ui', text: server.transactionActive});
        var td3 = $('<td/>', {'class': 'ui', text: server.transactionTotal});
        var td4 = $('<td/>', {'class': 'ui', text: server.transactionCommitted});
        var td5 = $('<td/>', {'class': 'ui', text: server.transactionRolledBack});

        // Add row
        var tr = $('<tr/>', {'class': "ui high"}).append(td1, td2, td3, td4, td5);
        jtaTable.append(tr);
    });

    // Return table
    return div;
}

function jdbcDiv(servers) {
    var div = $('<div/>');

    // Create a Server table
    var serverTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Server'});
    var td2 = $('<td/>', {'class': 'ui center', text: 'State'});
    var td3 = $('<td/>', {'class': 'ui center', text: 'Open Sockets', title: 'The current number sockets on the server that are open and receiving requests'});
    serverTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3));
    div.append(serverTable);

    // If no servers defined
    if (!servers || !servers.length) {
        var td1 = $('<td/>', {'colspan': 5}).append("No Servers found");
        serverTable.append($('<tr/>', {'class': "ui"}).append(td1));
        return div;
    }

    // For each server
    $.each(servers, function() {
        var server = this;

        // Data columns
        var tr = $('<tr/>', {'class': "ui high"});
        serverTable.append(tr);
        tr.append($('<td/>', {'class': 'ui', text: server.serverName}));
        tr.append($('<td/>', {'class': 'ui', text: server.state}));
        tr.append($('<td/>', {'class': 'ui', text: server.openSocket}));

        // Header
        var h2 = $('<h2/>', {'class': "center", text: server.serverName});
        div.append(h2);

        // Create a JDBC table
        var jdbcTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
        var td1 = $('<td/>', {'class': 'ui hostname', text: 'Name'});
        var td2 = $('<td/>', {'class': 'ui center', text: 'Current', title: 'The current number of active connections in a JDBC connection pool'});
        var td3 = $('<td/>', {'class': 'ui center', text: 'High', title: 'The highest number of active connections in a JDBC connection pool'});
        var td4 = $('<td/>', {'class': 'ui center', text: 'Failures', title: 'The number of times that the connection pool failed to reconnect to a data store'});
        var td5 = $('<td/>', {'class': 'ui center', text: 'Leaked', title: 'The total number of connections that have been checked out of, but not returned to, the connection pool'});
        var td6 = $('<td/>', {'class': 'ui center', text: 'Capacity', title: 'The current number of database connections in the JDBC connection pool'});
        var td7 = $('<td/>', {'class': 'ui center', text: 'Available', title: 'The number of available sessions in the session pool that are not currently being used'});
        var td8 = $('<td/>', {'class': 'ui center', text: 'Waiting', title: 'The current number of requests that are waiting for a connection to the connection pool'});
        jdbcTable.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6, td7, td8));
        div.append(jdbcTable);

        // For each server
        $.each(server.dataSource, function() {
            var dataSource = this;

            // Data columns
            var tr = $('<tr/>', {'class': "ui high"});
            jdbcTable.append(tr);
            tr.append($('<td/>', {'class': 'ui', text: dataSource.sourceName}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.current}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.high}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.failures}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.leaked}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.capacity}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.available}));
            tr.append($('<td/>', {'class': 'ui', text: dataSource.waiting}));

            // Error
            if (dataSource.leaked > 0) {
                tr.addClass('ui-state-error');
            }
        });
    });

    // Return table
    return div;
}
