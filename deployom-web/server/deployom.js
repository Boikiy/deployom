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
var LANG = {
    ok: "Ok",
    error: "Error",
    authError: "For Authorized personnel use ONLY. Please Authenticate",
    remoteAuthError: "Please Authenticate on Remote Site",
    authAdmin: "You have to login as Admin",
    accessDenied: "Access denied",
    siteDisabled: "Updating is Disabled",
    disabled: "Disabled",
    loading: "Loading...",
    notExecuted: "Not executed yet",
    noGui: "Service GUI are not found",
    noCharts: "No Charts are defined",
    noConfiguration: "Service Configurations are not found",
    noDatabase: "Databases are not found",
    noModules: "Modules are not found",
    noReports: "Service Reports are not found or filtered",
    noFlows: "No Flows definition are found",
    noEvents: "No Events are registered!",
    noOperations: "No Operations are registered!",
    noJobs: "No Jobs definition are found",
    noConnections: "No Connections definition are found",
    noHosts: "No Hosts are found",
    noErrors: "No Errors are found",
    noServices: "No Services are found",
    noMessages: "No Messages are found",
    noCommands: "No Commands are defined",
    noOverridden: "No Commands are overridden. Check linked Service for details",
    noSites: "No Sites are defined",
    noReleases: "No Releases are defined",
    noUsers: "No Users are defined",
    running: "Running...",
    siteAdded: "Site is added. Please restart application to schedule periodic jobs after initial configuration",
    submitError: "There is an Error while submit",
    connectionError: "Connection Error. Verify Network"
};

$(function() {

    // Check Cookies
    if (!$.cookie("userName") || !$.cookie("password")) {
        loginDialog();
        return true;
    }

    // Disable ajax caching
    $.ajaxSetup({cache: false});

    // Open Config
    $.ajax({
        type: "GET",
        dataType: "json",
        url: "/jersey/Config/getConfig",
        error: function(jqXHR) {
            if (jqXHR.status === 0) {
                notificationMessage(LANG.connectionError);
            } else if (jqXHR.status === 401) {
                notificationMessage(LANG.authError);
                loginDialog();
                return true;
            } else {
                notificationMessage(LANG.submitError);
            }
        },
        success: function(config) {

            // Set on Page
            $('#now').append(config.now);
            $('#title').text(config.title);
            $('#version').append(config.version);
            $('#envName').text(config.envName);
            document.title = config.envName + "/" + document.title;

            // Check Users
            $.each(config.user, function() {
                var user = this;

                // Skip if disabled
                if ($.cookie("userName") !== user.userName || !user.role) {
                    return true;
                }

                // Set buttonset
                $("#headSelect").buttonset();

                // Set home
                $('#home').button({icons: {primary: "deployom-home"}}).click(function(event) {
                    location.replace('/');
                });

                // Set dashboard
                $('#dashboard').button({icons: {primary: "deployom-dashboard"}}).click(function(event) {
                    location.replace('/dashboard');
                });

                // Legend buttong
                $('#legend-ok').button();
                $('#legend-error').button();

                // Show Header and Footer
                $('#header').show();
                $('#footer').show();

                // Hide Menu
                $('#menu').menu().hide();

                // Do After Login
                $('#tabsDiv').prepend(afterLogin(config, user.role));
                $("#tabsDiv").tabs();

                // Parse Tab from URL
                var tab = window.location.hash.substr(1);
                if (tab) {
                    $('#A_' + tab).click();
                } else if ($(".ui-tabs-active").text()) {
                    $('#A_' + $(".ui-tabs-active").text()).click();
                }

                // Enable SSE notifications
                if (typeof (EventSource) === "undefined") {
                    notificationMessage('Notifications Disabled, SSE not supported', "ui-state-error");
                    return true;
                } else {
                    // Register
                    var source = new EventSource("/jersey/History/addBroadcaster");

                    // Show Notification
                    source.onmessage = function(event) {
                        notificationMessage(event.data, "ui-state-highlight");
                    };

                    // On open, console logging
                    source.onopen = function(event) {
                        console.log('Notifications Enabled');
                    };

                    // Error
                    source.onerror = function(event) {
                        notificationMessage('Notifications Disabled, Error occurred', "ui-state-error");
                    };
                }
            });
        }
    });
});

function notificationMessage(message, state) {

    // Set Div
    var dialog = $("<div/>", {text: message});

    // Notify Dialog
    dialog.dialog({
        autoOpen: false,
        modal: false,
        width: 320,
        height: 60,
        show: 'fade',
        hide: 'fade',
        dialogClass: 'notify',
        position: {my: "right bottom", at: "right bottom", of: window}
    }).dialog("open").parent().addClass(state);

    // Close after 10 seconds
    setTimeout(function() {
        dialog.dialog('close');
    }, 10000);

    // Return
    return dialog;
}

function broadcastMessage(site, message) {

    var url = "/jersey/History/broadcastMessage";

    // If Remote Site
    if (site.serverURL) {
        url = site.serverURL + url;
    }

    // AJAX
    $.ajax({
        url: url,
        type: "POST",
        data: {Message: message},
        xhrFields: {
            withCredentials: true
        },
        error: function() {
            notificationMessage(LANG.submitError);
        }
    });
}

function chartDialog(site, hostName, serviceName, commandId, title) {

    // URL
    var url = "/jersey/Host/getChart";

    // If Remote Site
    if (site.serverURL) {
        url = site.serverURL + url;
    }

    // Set Dialog
    var chartDiv = $("<div/>", {text: LANG.loading});
    var dialog = $("<div/>").append(chartDiv);

    // Create Dialog
    dialog.dialog({
        autoOpen: true,
        modal: true,
        closeOnEscape: true,
        height: 420,
        width: 780,
        title: title + " [" + serviceName + "]",
        buttons: {
            Refresh: function() {
                chartDialog(site, hostName, serviceName, commandId, title);
                $(this).dialog("close");
            },
            Close: function() {
                $(this).dialog("close");
            }
        }
    });

    // AJAX
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {SiteName: site.siteName, HostName: hostName, ServiceName: serviceName, CommandId: commandId},
        xhrFields: {
            withCredentials: true
        },
        success: function(chart) {
            chartDiv.empty();

            var lines = [];
            var series = [];

            // Adding Lines and Labels
            for (var i = 1; i <= 3; i++) {
                series.push({label: chart['label' + i]});
                lines.push(chart['line' + i]);
            }

            chartDiv.jqplot(lines, {
                seriesColors: ['#73C774', '#C7754C', '#EAA228'],
                title: title + " [" + serviceName + "]",
                seriesDefaults: {
                    pointLabels: {show: true, stackedValue: false}
                },
                series: series,
                legend: {
                    show: true,
                    placement: 'insideGrid'
                },
                noDataIndicator: {
                    show: true,
                    indicator: 'No data found'
                },
                axesDefaults: {
                    min: 0
                },
                axes: {
                    xaxis: {renderer: $.jqplot.CategoryAxisRenderer,
                        ticks: chart.tick}
                }});
        }
    });

    // Return
    return dialog;
}

function loginDialog() {

    // Form
    var form = $("<form/>");
    form.append($("<p/>", {text: 'Please authenticate.'}).append($("<span/>", {'class': "ui-icon ui-icon-alert"})));
    var fieldset = $("<fieldset/>", {'class': "ui-helper-reset"});

    // UserName
    fieldset.append($('<label/>', {text: 'User Name'}));
    var loginUserName = $('<input/>', {type: 'text', name: 'UserName', id: 'loginUserName', 'class': "ui-widget-content ui-corner-all"});
    fieldset.append(loginUserName);

    // Password
    fieldset.append($('<label/>', {text: 'Password'}));
    var loginPassword = $('<input/>', {type: 'password', name: 'Password', id: 'loginPassword', 'class': "ui-widget-content ui-corner-all"});
    fieldset.append(loginPassword);
    form.append(fieldset);

    // Dialog
    var dialog = $("<div/>", {title: "Login", 'class': 'dialog'}).append(form);

    dialog.dialog({
        autoOpen: true,
        modal: true,
        width: 400,
        minHeight: 260,
        height: 260,
        draggable: false,
        closeOnEscape: false,
        resizable: false,
        dialogClass: 'login',
        open: function() {
            dialog.keypress(function(e) {
                if (e.keyCode === $.ui.keyCode.ENTER) {
                    $(this).parent().find("button").click();
                }
            });
        },
        buttons: {
            Login: function() {

                // Check User Name
                if (loginUserName.val() === "") {
                    alert('Please enter User Name');
                    return false;
                }

                // Check Password
                if (loginPassword.val() === "") {
                    alert('Please enter Password');
                    return false;
                }

                // Set cookie
                $.cookie("userName", loginUserName.val(), {expires: 30, path: '/'});
                $.cookie("password", loginPassword.val(), {expires: 30, path: '/'});

                // Go to Server
                location.reload();
            }
        }
    });

    return dialog;
}

function commandDialog(site, hostName, serviceName, commandId, title, exec) {

    // URL
    var url = "/jersey/Host/runCommand";

    // If Remote Site
    if (site.serverURL) {
        url = site.serverURL + url;
    }

    // Set Div
    var commandPre = $('<pre/>', {text: "=> " + hostName + " [" + serviceName + "]: " + exec});
    var outputPre = $('<pre/>', {text: LANG.loading});
    var dialog = $("<div/>").append(commandPre, outputPre);

    dialog.dialog({
        autoOpen: true,
        modal: true,
        closeOnEscape: true,
        height: 620,
        width: 830,
        title: title + " [" + serviceName + "] on " + hostName,
        buttons: {
            Refresh: function() {
                commandDialog(site, hostName, serviceName, commandId, title, exec);
                $(this).dialog("close");
            },
            Close: function() {
                $(this).dialog("close");
            }
        }
    });

    // AJAX
    $.ajax({
        url: url,
        dataType: "json",
        type: "POST",
        data: {SiteName: site.siteName, HostName: hostName, ServiceName: serviceName, CommandId: commandId},
        xhrFields: {
            withCredentials: true
        },
        success: function(host) {

            // Empty Dialog
            dialog.empty();

            // If Host is null
            if (!host) {
                dialog.text('Host Error');
                return true;
            }

            // For each service
            $.each(host.service, function() {
                var service = this;

                // For each command
                $.each(service.command, function() {
                    var command = this;

                    // Add output
                    var commandPre = $('<pre/>', {text: "=> " + host.hostName + " [" + service.serviceName + "]: " + command.exec});
                    var outputPre = $('<pre/>', {text: command.out});
                    dialog.append(commandPre, outputPre);

                    // Add Class for Errors
                    if (command.error) {
                        outputPre.addClass("ui-state-error");
                    }
                });
            });
        },
        error: function() {
            notificationMessage(LANG.submitError);
        }
    });

    // Return
    return dialog;
}

function getServiceMenu(site, service) {
    var serviceName = service.serviceName;

    // Menu
    var serviceMenu = [];

    // Additional Menu
    var chartMenu = $('<ul/>');
    var anyChart = false;
    var configurationMenu = $('<ul/>');
    var anyConfiguration = false;
    var databaseMenu = $('<ul/>');
    var anyDatabase = false;
    var guiMenu = $('<ul/>');
    var anyGui = false;
    var operationsMenu = $('<ul/>');
    var anyOperation = false;
    var reportsMenu = $('<ul/>');
    var anyReport = false;

    // For each Chart
    $.each(service.chart, function() {
        var chart = this;

        var li = $('<li/>', {text: chart.title}).click(function() {
            $('#menu').hide();

            // Show Chart
            chartDialog(site, service.hostName, serviceName, chart.chartId, chart.title);
        });

        // Set Service Icon
        setServiceImage(chart.title, li);

        // Add into Chart menu
        chartMenu.append(li);
        anyChart = true;
    });

    // For each Command
    $.each(service.command, function() {
        var command = this;
        var group = command.group;

        // If title or group are not defined
        if (!command.title) {
            return true;
        }

        // If gui properties defined
        if (group && group.match(/GUI/i)) {

            // Create a link
            var a = $('<a/>', {text: command.title, target: '_blank', href: command.exec.replace('$IP', service.ip)});

            // Set Menu Icon
            setServiceImage(command.title, a);

            // Add into GUI menu
            guiMenu.append($("<li/>").append(a));
            anyGui = true;

            return true;
        }

        // Command
        var li = $('<li/>', {text: command.title}).click(function() {
            $('#menu').hide();
            commandDialog(site, service.hostName, serviceName, command.commandId, command.title, command.exec);
        });

        // Ask to confirm for Operations
        if (group && group.match(/Operations/i)) {
            li = $('<li/>', {text: command.title}).click(function() {

                $('#menu').hide();

                // Set Div
                var alertP = $("<p/>", {text: "Please confirm command execution."}).append($("<p/>", {'class': 'ui-icon ui-icon-alert'}));
                var commandPre = $("<pre/>", {text: service.hostName + " [" + service.serviceName + "]: " + command.exec});

                var dialog = $("<div/>").append(alertP, commandPre);

                dialog.dialog({
                    autoOpen: true,
                    modal: true,
                    width: 560,
                    title: "Execute Command Confirmation",
                    buttons: {
                        Execute: function() {
                            commandDialog(site, service.hostName, serviceName, command.commandId, command.title, command.exec);
                            $(this).dialog("close");
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        }

        // Set Menu Icon
        setServiceImage(command.title, li);

        if (!group) {
            serviceMenu.push(li);
        }
        else if (group.match(/Database/i)) {
            databaseMenu.append(li);
            anyDatabase = true;

            return true;
        }
        else if (group.match(/Operations/i)) {
            operationsMenu.append(li);
            anyOperation = true;

            return true;
        }
        else if (group.match(/Configuration/i)) {
            configurationMenu.append(li);
            anyConfiguration = true;

            return true;
        }
        else if (group.match(/Reports/i)) {
            reportsMenu.append(li);
            anyReport = true;

            return true;
        }
        else {
            serviceMenu.push(li);
        }
    });

    // If Chart found
    if (anyChart) {
        serviceMenu.push($('<li/>', {text: 'Chart'}).append(chartMenu));
    }

    // If configuration found
    if (anyConfiguration) {
        serviceMenu.push($('<li/>', {text: 'Configuration'}).append(configurationMenu));
    }

    // If database found
    if (anyDatabase) {
        serviceMenu.push($('<li/>', {text: 'Database'}).append(databaseMenu));
    }

    // If gui found
    if (anyGui) {
        serviceMenu.push($('<li/>', {text: 'GUI'}).append(guiMenu));
    }

    // If operations found
    if (anyOperation) {
        serviceMenu.push($('<li/>', {text: 'Operations'}).append(operationsMenu));
    }

    if (anyReport) {
        serviceMenu.push($('<li/>', {text: 'Reports'}).append(reportsMenu));
    }

    // Return
    return serviceMenu;
}

function showMenu(menu, button) {
    $('#menu').empty();
    $('#menu').append(menu);
    $('#menu').menu("refresh");
    $('#menu').show().position({my: "left top", at: "left bottom", of: button});
}

function drawServiceConnections(canvas, connections, table, startButton) {

    // Set position and dimension
    canvas.position({my: "left top", at: "left top", of: table});

    // Define context
    var ctx = canvas[0].getContext("2d");
    ctx.canvas.width = table.width();
    ctx.canvas.height = table.height();
    ctx.lineJoin = "round";
    ctx.lineCap = "round";

    // Clear Area
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

    // Looking for connections
    $.each(connections, function() {
        var connection = this;
        var startServiceName = connection.start.serviceName;
        var startHostName = connection.start.hostName;
        var endServiceName = connection.end.serviceName;
        var endHostName = connection.end.hostName;

        // STRAIGHT 
        if (startButton.data('serviceName').match(startServiceName)) {

            // If host is not matching
            if (startHostName && startHostName !== 'equal' && !startButton.data('hostName').match(startHostName)) {
                return true;
            }

            // Looking for all buttons
            table.find(":button").each(function() {
                var endButton = $(this);

                if (!endButton.data('serviceName').match(endServiceName)) {
                    return true;
                }

                if (startHostName && startHostName === "equal" && startButton.data('hostName') !== endButton.data('hostName')) {
                    return true;
                }

                if (endHostName && !endButton.data('hostName').match(endHostName)) {
                    return true;
                }

                // Draw
                if (startButton.hasClass('ui-state-error') || endButton.hasClass('ui-state-error')) {
                    drawConnection(ctx, table, startButton, endButton, '#cd0a0a', 1);
                } else {
                    drawConnection(ctx, table, startButton, endButton, '#10bf1c', 1);
                }
            });
        }

        // REVERSED
        else if (startButton.data('serviceName').match(endServiceName)) {

            // If host is not matching
            if (endHostName && !startButton.data('hostName').match(endHostName)) {
                return true;
            }

            // Looking for all buttons
            table.find(":button").each(function() {
                var endButton = $(this);

                if (!endButton.data('serviceName').match(startServiceName)) {
                    return true;
                }

                if (startHostName && startHostName === "equal" && startButton.data('hostName') !== endButton.data('hostName')) {
                    return true;
                }

                if (startHostName && startHostName !== 'equal' && !endButton.data('hostName').match(startHostName)) {
                    return true;
                }

                // Draw
                if (startButton.hasClass('ui-state-error') || endButton.hasClass('ui-state-error')) {
                    drawConnection(ctx, table, endButton, startButton, '#cd0a0a', 1);
                } else {
                    drawConnection(ctx, table, endButton, startButton, '#10bf1c', 1);
                }
            });
        }
    });
}

function drawConnection(ctx, where, start, end, style, width) {

    // length of head in pixels
    var head = 4;
    var offset = 7;
    var slide = 10;

    // Set Style
    ctx.strokeStyle = style;
    ctx.lineWidth = width;

    // Relative Canvas Coordinates
    var startx = start.offset().left - where.offset().left + start.width() / 2;
    var starty = start.offset().top - where.offset().top;

    // Relative Canvas Coordinates
    var endx = end.offset().left - where.offset().left + end.width() / 2;
    var endy = end.offset().top - where.offset().top;

    // Begin
    ctx.beginPath();

    // Starting position, straigh line from left to right
    if (starty === endy && startx < endx) {
        ctx.moveTo(startx + start.width() / 2, starty + start.height() / 2);
        ctx.lineTo(endx - end.width() / 2, endy + start.height() / 2);

        // Arrow
        ctx.lineTo(endx - head - end.width() / 2, endy + start.height() / 2 - head);
        ctx.moveTo(endx - end.width() / 2, endy + start.height() / 2);
        ctx.lineTo(endx - head - end.width() / 2, endy + start.height() / 2 + head);

        // Stroke
        ctx.stroke();
        ctx.closePath();

        return true;
    }

    if (startx > endx) {
        ctx.moveTo(startx - slide, starty + start.height());
        ctx.lineTo(startx - slide, starty + start.height() + offset);
        ctx.lineTo(startx - start.width() / 2 - offset + 2, starty + start.height() + offset);
        ctx.lineTo(startx - start.width() / 2 - offset + 2, endy - offset);
    }
    else if (startx < endx) {
        ctx.moveTo(startx + slide, starty + start.height());
        ctx.lineTo(startx + slide, starty + start.height() + offset);
        ctx.lineTo(startx + start.width() / 2 + offset, starty + start.height() + offset);
        ctx.lineTo(startx + start.width() / 2 + offset, endy - offset);
    } else if (starty < endy) {
        ctx.moveTo(startx, starty + start.height());
    } else {
        ctx.moveTo(startx, starty + start.height());
        ctx.lineTo(startx, starty + start.height() + offset);
        ctx.lineTo(startx + start.width() / 2 + offset, starty + start.height() + offset);
        ctx.lineTo(startx + start.width() / 2 + offset, endy - offset);
    }

    // Ending position
    ctx.lineTo(endx, endy - offset);

    // Arrow
    ctx.lineTo(endx, endy);
    ctx.lineTo(endx + head, endy - head);
    ctx.lineTo(endx, endy);
    ctx.lineTo(endx - head, endy - head);

    // Stroke
    ctx.stroke();
    ctx.closePath();
}
