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

    // Set dashboard
    $('#dashboard').button({icons: {primary: "deployom-dashboard"}}).click(function(event) {
        location.replace('/dashboard');
    });

    // If no Sites
    if (!config.site.length) {
        return $('<div/>', {'text': LANG.noSites});
    }

    // Site tabs
    var ul = $('<ul/>');
    var firstTab;

    $.each(config.site, function() {
        var configSite = this;

        // Add Site tab
        var siteLi = $('<li/>');
        var a = $('<a/>', {id: 'A_' + configSite.siteName, text: configSite.siteName, href: '#' + configSite.siteName, title: 'Click to Refresh'});
        siteLi.append(a);
        ul.append(siteLi);

        // First Tab
        if (!firstTab) {
            firstTab = a;
        }

        // If updating disabled
        if (!configSite.enabled) {
            a.append(' [DISABLED]');
        }

        $("#tabsDiv").append($('<div/>', {id: configSite.siteName}));

        // Set onclick
        a.click(function() {
            $('#' + configSite.siteName).empty();
            $('#' + configSite.siteName).prepend(dashboardTab(configSite, ul));
        });
    });

    // Show Site
    if (firstTab) {
        firstTab.click();
    }

    // Return
    return ul;
}

function dashboardTab(configSite, ul) {
    var siteName = configSite.siteName;

    // Site div
    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    var h1 = $('<h1/>', {'class': "center", text: siteName});
    div.append(h1);

    // Add Divs
    var flowsDiv = $('<div/>');
    var eventsDiv = $('<div/>');
    div.append(flowsDiv, eventsDiv);

    var url = "/jersey/Site/getFlows";
    if (configSite.serverURL) {
        url = configSite.serverURL + url;
    }

    // Request Flows
    $.ajax({
        url: url,
        type: "POST",
        data: {SiteName: siteName},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        beforeSend: function(data) {
            flowsDiv.empty();
            flowsDiv.prepend(LANG.loading);
        },
        success: function(site) {
            flowsDiv.empty();
            site.serverURL = configSite.serverURL;
            flowsDiv.prepend(flowsTable(site, ul));
            flowsDiv.append(modulesTable(site));
        },
        error: function() {
            flowsDiv.empty();
            flowsDiv.prepend(LANG.submitError);
        }
    });

    var siteUrl = "/jersey/Site/getEvents";
    if (configSite.serverURL) {
        siteUrl = configSite.serverURL + siteUrl;
    }

    // Request Events
    $.ajax({
        url: siteUrl,
        type: "POST",
        data: {SiteName: siteName},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        beforeSend: function(data) {
            eventsDiv.empty();
            eventsDiv.prepend(LANG.loading);
        },
        success: function(site) {
            eventsDiv.empty();
            site.serverURL = configSite.serverURL;
            eventsDiv.prepend(eventsTable(site));
        },
        error: function() {
            eventsDiv.empty();
            eventsDiv.prepend(LANG.submitError);
        }
    });

    // Return
    return div;
}

function flowsTable(site, ul) {

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui'});
    table.append($('<tr/>', {'class': "ui"}).append(td1));

    // Check if no flows
    if (!site.flow.length) {
        td1.text(LANG.noFlows);
        return table;
    }

    // For each flow
    $.each(site.flow, function() {
        var flow = this;
        var flowName = flow.flowName;

        // Create a flow Button
        var flowButton = $('<button/>', {"class": "flow", text: flowName, title: 'No Services'});

        // Set Button Icon
        setFlowImage(flowButton);

        // For each Host
        $.each(flow.host, function() {
            var host = this;

            // If services defined
            if (host.service.length) {
                flowButton.addClass("ui-state-ok");
                flowButton.attr('title', 'All Services are Online');
            }

            // Check offline Services
            $.each(host.service, function() {
                var service = this;

                // Add Class
                if (!service.online) {
                    flowButton.addClass('ui-state-error');
                    flowButton.attr('title', 'Offline Services found');
                    return true;
                }
            });
        });

        // Add Flow Click
        flowButton.click(function() {

            // Add tab for Flow
            var li = $('<li/>');
            var a = $('<a/>', {text: flowName + " [" + site.siteName + "]", href: '#' + flowName});
            var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
            span.click(function(event) {
                li.remove();
                $('#A_' + site.siteName).click();
            });
            li.append(a, span);
            ul.append(li);
            $("#tabsDiv").append($('<div/>', {id: flowName}));

            // On click
            a.click(function() {

                var url = "/jersey/Site/getFlow";
                if (site.serverURL) {
                    url = site.serverURL + url;
                }

                // Request Flow
                $.ajax({
                    url: url,
                    type: "POST",
                    data: {SiteName: site.siteName, FlowName: flowName},
                    dataType: "json",
                    xhrFields: {
                        withCredentials: true
                    },
                    beforeSend: function(data) {
                        $('#' + flowName).empty();
                        $('#' + flowName).prepend(LANG.loading);
                    },
                    success: function(flow) {
                        $('#' + flowName).empty();

                        // Create a div and append
                        var div = $('<div/>').click(function() {
                            $('#menu').hide();
                        });
                        $('#' + flowName).append(div);

                        flowTab(flow, site, div);
                    },
                    error: function() {
                        $('#' + flowName).empty();
                        $('#' + flowName).prepend(LANG.submitError);
                    }
                });
            });

            // Click to show
            a.click();
            $("#tabsDiv").tabs('refresh');
            $("#tabsDiv").tabs({active: -1});
        });

        td1.append(flowButton);
    });

    // Return
    return table;
}

function modulesTable(site) {

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui center', text: 'Service Modules'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1));
    var td1 = $('<td/>', {'class': 'ui'});
    table.append($('<tr/>', {'class': "ui"}).append(td1));

    // Check if no modules
    if (!site.module.length) {
        td1.text(LANG.noModules);
        return table;
    }

    // For each module
    $.each(site.module, function() {
        var module = this;

        // Skip modules w/o context
        if (!module.context) {
            return true;
        }

        // Module
        var moduleButton = $('<button/>', {text: module.moduleName.toUpperCase(), 'class': 'flow'});
        setModuleImage(moduleButton).click(function() {
            location.replace(module.context + '#' + site.siteName);
        });

        td1.append(moduleButton);
    });

    // Return
    return table;
}

function flowTab(flow, site, div) {

    var h1 = $('<h1/>', {'class': "center", text: flow.flowName});
    div.append(h1);

    // Create a Flow table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    div.append(table);

    // Create Reports table
    var reportsTable = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Service'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Report'}));
    reportsTable.append(tr);

    // Create Configuration table
    var configurationTable = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Service'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Configuration'}));
    configurationTable.append(tr);

    // Create Database table
    var databaseTable = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Service'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Database'}));
    databaseTable.append(tr);

    // Detailed options
    var anyReports = false;
    var anyConfiguration = false;
    var anyDatabase = false;

    // For each host
    $.each(flow.host, function() {
        var host = this;

        // Add Host
        var td1 = $('<td/>', {'class': 'ui IP', text: host.hostName.replace(/\([\S+\s+]+\)/, "")});
        var td2 = $('<td/>', {'class': 'ui'});
        table.append($('<tr/>').append(td1, td2));

        // For Each Service
        $.each(host.service, function() {
            var service = this;
            var serviceName = service.serviceName;
            var hostName = service.hostName;

            // Create service button
            var serviceButton = $('<button/>', {text: serviceName + ' [' + hostName + ']', 'class': 'flowService'});
            td2.append(serviceButton);

            // Set data
            serviceButton.data('serviceName', serviceName);
            serviceButton.data('hostName', hostName);

            // Set Service Icon
            setServiceIcon(serviceButton, 'ui-icon-triangle-1-s').click(function() {
                $('#menu').empty();
                $('#menu').append(getServiceMenu(site, service));
                $('#menu').menu("refresh");
                $('#menu').show().position({
                    my: "left top",
                    at: "left bottom",
                    of: this
                });
                return false;
            });

            // Add Service Class
            if (!service.online) {
                serviceButton.addClass('ui-state-error');
            } else {
                serviceButton.addClass('ui-state-ok');
            }

            // Create a row for reports
            var reportsTr = $('<tr/>');
            reportsTr.append($('<td/>', {'class': 'ui hostname', text: serviceName + " [" + hostName + "]"}));
            var reportsTd = $('<td/>', {'class': 'ui'});
            reportsTr.append(reportsTd);

            // Create a row for configuration
            var configurationTr = $('<tr/>');
            configurationTr.append($('<td/>', {'class': 'ui hostname', text: serviceName + " [" + hostName + "]"}));
            var configurationTd = $('<td/>', {'class': 'ui'});
            configurationTr.append(configurationTd);

            // Create a row for database
            var databaseTr = $('<tr/>');
            databaseTr.append($('<td/>', {'class': 'ui hostname', text: serviceName + " [" + hostName + "]"}));
            var databaseTd = $('<td/>', {'class': 'ui'});
            databaseTr.append(databaseTd);

            var reportsService = false;
            var configurationService = false;
            var databaseService = false;

            // For each Command
            $.each(service.command, function() {
                var command = this;
                var title = command.title;
                var group = command.group;

                // Create Command button
                var commandButton = $('<button/>', {"class": "report", text: command.title});

                // Set Button Icon
                setServiceIcon(commandButton, 'ui-icon-newwin').click(function() {
                    commandDialog(site, hostName, serviceName, command.commandId, command.title, command.exec);
                });

                // Add Class
                if (command.error || !service.online) {
                    commandButton.addClass('ui-state-error');
                }

                // Skip general
                if (!group) {
                    return true;
                }

                if (group.match(/Report/i)) {

                    // Filtered
                    if (!title.match(flow.filter)) {
                        return true;
                    }

                    reportsTd.append(commandButton);
                    reportsService = true;
                    anyReports = true;

                } else if (group.match(/Configuration/i)) {

                    configurationTd.append(commandButton);
                    configurationService = true;
                    anyConfiguration = true;

                } else if (group.match(/Database/i)) {

                    databaseTd.append(commandButton);
                    databaseService = true;
                    anyDatabase = true;
                }
            });

            if (reportsService) {
                reportsTable.append(reportsTr);
            }

            if (configurationService) {
                configurationTable.append(configurationTr);
            }

            if (databaseService) {
                databaseTable.append(databaseTr);
            }
        });
    });

    // Check if reports found
    if (anyReports) {
        var h2 = $('<h2/>', {'class': "center", text: 'Custom Reports'});
        div.append(h2, reportsTable);
    }

    // Check if configuration found
    if (anyConfiguration) {
        var h2 = $('<h2/>', {'class': "center", text: 'Service Configuration'});
        div.append(h2, configurationTable);
    }

    // Check if database found
    if (anyDatabase) {
        var h2 = $('<h2/>', {'class': "center", text: 'Database Information'});
        div.append(h2, databaseTable);
    }

    // Create a Canvas for table
    var canvas = $('<canvas/>', {'class': "flow"});
    div.append(canvas);

    // Draw Service Connections 
    $.ajax({
        url: "/jersey/Release/getConnections",
        type: "POST",
        dataType: "json",
        data: {ReleaseName: site.releaseName},
        success: function(release) {

            // If no connections
            if (!release.connection.length) {
                return true;
            }

            // Set MouseOver
            table.find(":button").each(function() {
                $(this).mouseover(function() {
                    drawServiceConnections(canvas, release.connection, table, $(this));
                });
            });
        }
    });
}

function eventsTable(site) {
    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Service Events'}));
    table.append(tr);

    // Events
    var siteEvents = false;

    // For each host
    $.each(site.host, function() {
        var host = this;

        // Add row
        var tr = $('<tr/>');
        tr.append($('<td/>', {'class': 'ui center', text: host.hostName}));
        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        var anyService = false;

        // For each event
        $.each(host.event, function() {
            var event = this;
            var serviceName = event.serviceName;

            // Create Service button
            var serviceButton = $('<button/>', {"class": "eventService ui-state-error",
                text: event.title + " [" + serviceName + "]"});

            // Set service icon
            setServiceIcon(serviceButton, 'ui-icon-newwin').click(function() {
                commandDialog(site, host.hostName, serviceName, event.commandId, event.title, event.exec);
            });

            // Set Tooltip
            serviceButton.tooltip({
                items: "[class]",
                content: function() {
                    var commandPre = $('<pre/>', {text: "=> " + host.hostName + " [" + serviceName + "]: " + event.exec});
                    var outputPre = $('<pre/>', {text: event.out});
                    return $('<div/>').append(commandPre, outputPre);
                }});

            // Create a row
            td.append(serviceButton);

            // Found event
            anyService = true;
            siteEvents = true;
        });

        if (anyService) {
            // Add row
            table.append(tr);
        }
    });

    // If no events
    if (!siteEvents) {
        var td1 = $('<td/>', {'colspan': 4}).append(LANG.noEvents);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // Return table
    return table;
}
