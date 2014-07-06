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

    $("#addHostDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                if ($('#addHostName').val() === "") {
                    alert('Please enter Host Name');
                    return false;
                }

                if ($('#addHostIP').val() === "") {
                    alert('Please enter Host IP');
                    return false;
                }

                var serverURL = $('#addHostServerURL').val();
                var url = "/jersey/Site/addHost";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $('#addHostForm').serialize(),
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(data) {
                        notificationMessage(data);
                        $('#A_' + $('#addHostSiteName').val() + '_LAYOUT').click();
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

    $("#removeSiteDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                // AJAX
                $.ajax({
                    url: "/jersey/Config/removeSite",
                    type: "POST",
                    data: $("#removeSiteForm").serialize(),
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

    $("#removeHostDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                var serverURL = $('#removeHostServerURL').val();
                var url = "/jersey/Site/removeHost";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $('#removeHostForm').serialize(),
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(data) {
                        notificationMessage(data);
                        $('#A_' + $('#removeHostSiteName').val() + '_LAYOUT').click();
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

    $("#removeEventsDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                var serverURL = $('#removeEventsServerURL').val();
                var url = "/jersey/Site/removeEvents";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $('#removeEventsForm').serialize(),
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    complete: function(data) {
                        $('#A_' + $('#removeEventsSiteName').val() + '_EVENTS').click();
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

    $("#updateHostDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                if ($('#updateHostIP').val() === "") {
                    alert('Please enter Host IP');
                    return false;
                }

                var serverURL = $('#updateHostServerURL').val();
                var url = "/jersey/Site/updateHost";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $("#updateHostForm").serialize(),
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(data) {
                        $('#A_' + $('#updateHostSiteName').val() + '_LAYOUT').click();
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

    $("#renameHostDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                if ($('#renameNewHostName').val() === "") {
                    alert('Please enter New Host Name');
                    return false;
                }

                var serverURL = $('#renameHostServerURL').val();
                var url = "/jersey/Site/renameHost";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $("#renameHostForm").serialize(),
                    xhrFields: {
                        withCredentials: true
                    },
                    error: function() {
                        notificationMessage(LANG.submitError);
                    },
                    success: function(data) {
                        $('#A_' + $('#renameHostSiteName').val() + '_LAYOUT').click();
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

    $("#getHostsDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Ok: function() {

                if ($('#getHostsHostName').val() === "") {
                    alert('Please select Host Name');
                    return false;
                }

                var serverURL = $('#getHostsServerURL').val();
                var url = "/jersey/Site/getHosts";

                // If Remote Site
                if (serverURL !== "") {
                    url = serverURL + url;
                }

                // AJAX
                $.ajax({
                    url: url,
                    type: "POST",
                    data: $('#getHostsForm').serialize(),
                    dataType: "json",
                    xhrFields: {
                        withCredentials: true
                    },
                    beforeSend: function(data) {
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').empty();
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').prepend(LANG.loading);
                    },
                    success: function(site) {
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').empty();
                        site.serverURL = serverURL;
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').prepend(hostsTab(site));

                        // Fill Host Types
                        $.ajax({
                            url: "/jersey/Release/getHosts",
                            type: "POST",
                            dataType: "json",
                            data: {ReleaseName: site.releaseName},
                            success: function(release) {

                                // If no Hosts defined
                                if (!release.host.length) {
                                    return true;
                                }

                                // Empty Seelect
                                $('#addHostType').empty();

                                // Add hosts
                                $.each(release.host, function() {
                                    var host = this;

                                    // Add into List
                                    $('#addHostType').append($('<option/>').val(host.hostType).text(host.hostType));
                                });
                            }
                        });
                    },
                    error: function() {
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').empty();
                        $('#' + $('#getHostsSiteName').val() + '_HOSTS').prepend(LANG.submitError);
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

    $("#uploadSiteDialog").dialog({
        autoOpen: false,
        modal: true,
        width: 400,
        buttons: {
            Upload: function() {
                $("#uploadSiteForm").submit();
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

    // Set sites
    $('#site').button({icons: {primary: "deployom-site"}}).click(function(event) {
        location.replace('/site');
    });

    // If no Sites
    if (!config.site.length) {
        return $('<div/>', {'text': LANG.noSites});
    }

    // Site tabs
    var ul = $('<ul/>');

    $.each(config.site, function() {
        var configSite = this;

        // Add Site tab
        var siteLi = $('<li/>');
        var a = $('<a/>', {id: 'A_' + configSite.siteName, text: configSite.siteName, href: '#' + configSite.siteName});
        siteLi.append(a);
        ul.append(siteLi);

        // If updating disabled
        if (!configSite.enabled) {
            a.append(' [DISABLED]');
        }

        $("#tabsDiv").append($('<div/>', {id: configSite.siteName}));

        // Set onclick
        a.click(function() {
            $('#' + configSite.siteName).empty();
            $('#' + configSite.siteName).prepend(siteTab(configSite, role, ul));
        });
    });

    // Return
    return ul;
}

function siteTab(configSite, role, ul) {
    var siteName = configSite.siteName;

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    var h1 = $('<h1/>', {'class': "center", text: siteName});
    div.append(h1);

    // Remote Site
    if (configSite.serverURL) {
        h1.append(" (remote ");
        h1.append($('<a/>', {text: configSite.serverURL, target: '_blank', href: configSite.serverURL}));
        h1.append(")");
    }

    // Create a table
    var siteTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var siteTd = $('<td/>', {'class': 'ui'});
    siteTable.append($('<tr/>', {'class': "ui"}).append(siteTd));
    div.append(siteTable);

    var jobsTable = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui center', text: 'Custom Jobs', colspan: 2}));
    jobsTable.append(tr);
    var jobsTd = $('<td/>', {'class': 'ui'});
    jobsTable.append($('<tr/>', {'class': "ui"}).append(jobsTd));
    div.append(jobsTable);

    // Add Hosts tab
    var hostsButton = $('<button/>', {"class": "flow",
        text: 'Add Hosts', 'title': 'Adding multiple Hosts from File'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_HOSTS', text: 'Add Hosts [' + siteName + ']', href: '#' + siteName + '_HOSTS'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_HOSTS'}));

        // Add onclick action
        a.click(function() {
            $("#getHostsSiteName").val(siteName);
            $("#getHostsServerURL").val(configSite.serverURL);
            $("#getHostsDialog").dialog("open");

            var siteUrl = "/jersey/Site/getSite";
            if (configSite.serverURL) {
                siteUrl = configSite.serverURL + siteUrl;
            }

            // Fill Hosts
            $.ajax({
                url: siteUrl,
                type: "POST",
                dataType: "json",
                data: {SiteName: configSite.siteName},
                success: function(site) {

                    // If no Hosts defined
                    if (!site.host.length) {
                        return true;
                    }

                    // Empty Select
                    $('#getHostsHostName').empty();

                    // Add hosts
                    $.each(site.host, function() {
                        var host = this;

                        // Add into List
                        $('#getHostsHostName').append($('<option/>').val(host.hostName).text(host.hostName));
                    });
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    hostsButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/add.png'}));
    siteTd.append(hostsButton);

    // Add Change Layout tab
    var layoutButton = $('<button/>', {"class": "flow",
        text: 'Change Layout', 'title': 'Adding and Removing Services manually'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_LAYOUT', text: 'Change Layout [' + siteName + ']', href: '#' + siteName + '_LAYOUT'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_LAYOUT'}));

        // Add onclick action
        a.click(function() {

            var layoutUrl = "/jersey/Site/getLayout";
            if (configSite.serverURL) {
                layoutUrl = configSite.serverURL + layoutUrl;
            }

            // AJAX
            $.ajax({
                url: layoutUrl,
                type: "POST",
                data: {SiteName: configSite.siteName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + siteName + '_LAYOUT').empty();
                    $('#' + siteName + '_LAYOUT').prepend(LANG.loading);
                },
                success: function(site) {
                    $('#' + siteName + '_LAYOUT').empty();
                    site.serverURL = configSite.serverURL;
                    $('#' + siteName + '_LAYOUT').prepend(layoutTab(site));

                    // Fill Host Types
                    $.ajax({
                        url: "/jersey/Release/getHosts",
                        type: "POST",
                        dataType: "json",
                        data: {ReleaseName: site.releaseName},
                        success: function(release) {

                            // If no Hosts defined
                            if (!release.host.length) {
                                return true;
                            }

                            // Empty Seelect
                            $('#addHostType').empty();
                            $('#updateHostType').empty();

                            // Add hosts
                            $.each(release.host, function() {
                                var host = this;

                                // Add into List
                                $('#addHostType').append($('<option/>').val(host.hostType).text(host.hostType));
                                $('#updateHostType').append($('<option/>').val(host.hostType).text(host.hostType));
                            });
                        }
                    });
                },
                error: function() {
                    $('#' + siteName + '_LAYOUT').empty();
                    $('#' + siteName + '_LAYOUT').prepend(LANG.submitError);
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    layoutButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/manager.png'}));
    siteTd.append(layoutButton);

    // Add Services Button
    var servicesButton = $('<button/>', {"class": "flow",
        text: 'Configured Services', 'title': 'All Configured Service with Commands'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_SERVICES', text: 'Configured Services [' + siteName + ']', href: '#' + siteName + '_SERVICES'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_SERVICES'}));

        // Add onclick action
        a.click(function() {

            var servicesUrl = "/jersey/Site/getServices";
            if (configSite.serverURL) {
                servicesUrl = configSite.serverURL + servicesUrl;
            }

            // AJAX
            $.ajax({
                url: servicesUrl,
                type: "POST",
                data: {SiteName: configSite.siteName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + siteName + '_SERVICES').empty();
                    $('#' + siteName + '_SERVICES').prepend(LANG.loading);
                },
                success: function(site) {
                    $('#' + siteName + '_SERVICES').empty();
                    site.serverURL = configSite.serverURL;
                    $('#' + siteName + '_SERVICES').append(servicesTab(site));
                },
                error: function() {
                    $('#' + siteName + '_SERVICES').empty();
                    $('#' + siteName + '_SERVICES').prepend(LANG.submitError);
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    servicesButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/config.png'}));
    siteTd.append(servicesButton);

    // Add Events Button
    var eventsButton = $('<button/>', {"class": "flow",
        text: 'Registered Events', 'title': 'All Registered Events'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_EVENTS', text: 'Registered Events [' + siteName + ']', href: '#' + siteName + '_EVENTS'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_EVENTS'}));

        // Add onclick action
        a.click(function() {

            var eventsUrl = "/jersey/Site/getEvents";
            if (configSite.serverURL) {
                eventsUrl = configSite.serverURL + eventsUrl;
            }

            // AJAX
            $.ajax({
                url: eventsUrl,
                type: "POST",
                data: {SiteName: configSite.siteName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + siteName + '_EVENTS').empty();
                    $('#' + siteName + '_EVENTS').prepend(LANG.loading);
                },
                success: function(site) {
                    $('#' + siteName + '_EVENTS').empty();
                    site.serverURL = configSite.serverURL;
                    $('#' + siteName + '_EVENTS').prepend(eventsTab(site));
                },
                error: function() {
                    $('#' + siteName + '_EVENTS').empty();
                    $('#' + siteName + '_EVENTS').prepend(LANG.submitError);
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    eventsButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/disabled.png'}));
    siteTd.append(eventsButton);

    // Add Service Discovery
    var discoveryButton = $('<button/>', {"class": "flow",
        text: 'Service Discovery', 'title': 'Automatic Service Discovery'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_DISCOVERY', text: 'Service Discovery [' + siteName + ']', href: '#' + siteName + '_DISCOVERY'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_DISCOVERY'}));

        // Add onclick action
        a.click(function() {

            var discoveryUrl = "/jersey/Job/getJob";
            if (configSite.serverURL) {
                discoveryUrl = configSite.serverURL + discoveryUrl;
            }

            // AJAX
            $.ajax({
                url: discoveryUrl,
                type: "POST",
                data: {SiteName: configSite.siteName, JobName: 'Discovery'},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + siteName + '_DISCOVERY').empty();
                    $('#' + siteName + '_DISCOVERY').prepend(LANG.loading);
                },
                success: function(job) {
                    $('#' + siteName + '_DISCOVERY').empty();
                    $('#' + siteName + '_DISCOVERY').prepend(discoveryTab(configSite, job));
                },
                error: function() {
                    $('#' + siteName + '_DISCOVERY').empty();
                    $('#' + siteName + '_DISCOVERY').prepend(LANG.submitError);
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    discoveryButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/discovery.png'}));
    siteTd.append(discoveryButton);

    // Add Site Map Button
    var siteMapButton = $('<button/>', {"class": "flow",
        text: 'Site Map', 'title': 'Grouped Site Services'}).button().click(function() {

        var li = $('<li/>');
        var a = $('<a/>', {id: 'A_' + siteName + '_MAP', text: 'Site Map [' + siteName + ']', href: '#' + siteName + '_MAP'});
        var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
        span.click(function(event) {
            li.remove();
            $('#A_' + siteName).click();
        });
        li.append(a, span);
        ul.append(li);
        $("#tabsDiv").append($('<div/>', {id: siteName + '_MAP'}));

        // Add onclick action
        a.click(function() {

            var mapUrl = "/jersey/Site/getMap";
            if (configSite.serverURL) {
                mapUrl = configSite.serverURL + mapUrl;
            }

            // AJAX
            $.ajax({
                url: mapUrl,
                type: "POST",
                data: {SiteName: configSite.siteName},
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function(data) {
                    $('#' + siteName + '_MAP').empty();
                    $('#' + siteName + '_MAP').prepend(LANG.loading);
                },
                success: function(site) {
                    $('#' + siteName + '_MAP').empty();
                    site.serverURL = configSite.serverURL;
                    mapTab(site);
                },
                error: function() {
                    $('#' + siteName + '_MAP').empty();
                    $('#' + siteName + '_MAP').prepend(LANG.submitError);
                }
            });
        });

        // Show
        a.click();

        $("#tabsDiv").tabs('refresh');
        $("#tabsDiv").tabs({active: -1});
    });
    siteMapButton.prepend($('<img/>', {'class': 'flow', src: '/server/img/virtual.png'}));
    siteTd.append(siteMapButton);

    var jobsUrl = "/jersey/Site/getJobs";
    if (configSite.serverURL) {
        jobsUrl = configSite.serverURL + jobsUrl;
    }

    // AJAX
    $.ajax({
        url: jobsUrl,
        type: "POST",
        data: {SiteName: configSite.siteName},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        beforeSend: function(data) {
            jobsTd.append(LANG.loading);
        },
        success: function(site) {

            jobsTd.empty();

            // Check if no jobs
            if (!site.job.length) {
                jobsTd.append(LANG.noJobs);
            }

            // For each Job
            $.each(site.job, function() {
                var job = this;

                // Add job Button
                var jobButton = $('<button/>', {"class": "hostname", text: job.jobName});

                // If disabled
                if (!job.enabled) {
                    jobButton.append(' [DISABLED]');
                }

                // If running
                if (job.running) {
                    jobButton.append(' [RUNNING]');
                    jobButton.addClass('ui-state-highlight');
                }

                // Set Host Icon
                setServiceIcon(jobButton, 'ui-icon-extlink').click(function() {
                    var li = $('<li/>');
                    var a = $('<a/>', {id: 'A_' + siteName + '_' + job.jobName, text: job.jobName + ' [' + site.siteName + ']', href: '#' + siteName + '_' + job.jobName});
                    var span = $('<span/>', {'class': "ui-icon ui-icon-close", role: "presentation", text: "Remove Tab"});
                    span.click(function(event) {
                        li.remove();
                        $('#A_' + site.siteName).click();
                    });
                    li.append(a, span);
                    ul.append(li);
                    $("#tabsDiv").append($('<div/>', {id: siteName + '_' + job.jobName}));

                    // Onclick Host
                    a.click(function() {

                        $.ajax({
                            url: "/jersey/Job/getJob",
                            type: "POST",
                            data: {SiteName: siteName, JobName: job.jobName},
                            dataType: "json",
                            beforeSend: function(data) {
                                $('#' + siteName + '_' + job.jobName).empty();
                                $('#' + siteName + '_' + job.jobName).prepend(LANG.loading);
                            },
                            success: function(job) {
                                $('#' + siteName + '_' + job.jobName).empty();
                                $('#' + siteName + '_' + job.jobName).append(jobTab(configSite, job));
                            },
                            error: function() {
                                $('#' + siteName + '_' + job.jobName).empty();
                                $('#' + siteName + '_' + job.jobName).prepend(LANG.submitError);
                            }
                        });
                    });

                    // Show
                    a.click();

                    $("#tabsDiv").tabs('refresh');
                    $("#tabsDiv").tabs({active: -1});
                });
                jobsTd.append(jobButton);

                // If any host found
                if (job.host.length) {
                    jobButton.addClass('ui-state-ok');
                }

                // For each Host
                $.each(job.host, function() {
                    var host = this;

                    $.each(host.service, function() {
                        var service = this;

                        // For each command
                        $.each(service.command, function() {
                            var command = this;

                            // If no Error
                            if (!command.error) {
                                return true;
                            }

                            jobButton.addClass('ui-state-error');
                        });
                    });
                });
            });
        },
        error: function() {
            jobsTd.text(LANG.submitError);
        }
    });

    var downloadSite = $('<a/>', {text: "Download Site File"});
    setButtonIcon(downloadSite).click(function() {
        var url = "/jersey/Site/downloadSite";

        // If Remote Site
        if (configSite.serverURL) {
            url = configSite.serverURL + url;
        }

        // Download Form
        var form = $('<form/>', {method: "post", action: url});
        form.append($('<input/>', {name: "SiteName", value: siteName}));
        form.submit();
    });

    var uploadSite = $('<a/>', {text: "Upload Site File"});
    setButtonIcon(uploadSite).click(function() {
        $("#uploadSiteName").val(siteName);
        $("#uploadSiteDialog").dialog("open");
    });

    var updateSiteButton = $('<button/>', {text: "Disable Job Execution"});
    if (!configSite.enabled) {
        updateSiteButton.text("Enable Job Execution");
    }

    // Set Onclick
    setButtonIcon(updateSiteButton).click(function() {

        $.ajax({
            url: "/jersey/Config/updateSite",
            type: "POST",
            data: {SiteName: siteName, Enabled: !configSite.enabled},
            error: function() {
                notificationMessage(LANG.submitError);
            },
            complete: function(data) {
                location.reload();
            }
        });
    });

    var removeSiteButton = $('<button/>', {text: "Remove Site"});
    setButtonIcon(removeSiteButton).click(function() {
        $("#removeSiteName").val(siteName);
        $("#removeSiteDialog").dialog("open");
    });

    // Non-Admin
    if (role !== "admin") {
        layoutButton.button('disable');
        hostsButton.button('disable');
        discoveryButton.button('disable');
        uploadSite.button('disable');
        removeSiteButton.button('disable');
    }

    // If Remote
    if (configSite.serverURL) {
        uploadSite.button('disable');
        updateSiteButton.button('disable');
        removeSiteButton.button('disable');
    }

    // Return Div
    return div.append(downloadSite, uploadSite, updateSiteButton, removeSiteButton);
}

function mapTab(site) {

    // Create a Site table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui center', text: 'Site Map', colspan: 2}));
    table.append(tr);
    $('#' + site.siteName + '_MAP').append(table);

    // Check if no hosts
    if (!site.host.length) {
        var td1 = $('<td/>', {'colspan': 4}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each host
    $.each(site.host, function() {
        var host = this;

        var td1 = $('<td/>', {'class': 'ui IP', text: host.hostName.replace(/\([\S+\s+]+\)/, "")});
        var td2 = $('<td/>', {'class': 'ui'});
        table.append($('<tr/>').append(td1, td2));

        // For Each Service
        $.each(host.service, function() {
            var service = this;

            // Create Host button
            var serviceButton = $('<button/>', {text: service.serviceName + " [" + service.hostName + "]", 'class': 'flowService'});
            td2.append(serviceButton);

            // Set Host Icon
            setServiceIcon(serviceButton, null);

            // Set data
            serviceButton.data('serviceName', service.serviceName);
            serviceButton.data('hostName', service.hostName);

            if (!service.online) {
                serviceButton.addClass('ui-state-error');
            } else {
                serviceButton.addClass('ui-state-ok');
            }
        });
    });

    // Create a Canvas for table
    var canvas = $('<canvas/>', {'class': "flow"});
    $('#' + site.siteName + '_MAP').append(canvas);

    // Draw Service Connections
    $.ajax({
        type: "POST",
        dataType: "json",
        data: {ReleaseName: site.releaseName},
        url: "/jersey/Release/getConnections",
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

function eventsTab(site) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Service Events'}));
    table.append(tr);

    // Events
    var jobEvents = false;

    // For each host
    $.each(site.host, function() {
        var host = this;

        // Create a row
        var tr = $('<tr/>');
        tr.append($('<td/>', {'class': 'ui center', text: host.hostName}));
        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        // For each event
        $.each(host.event, function() {
            var event = this;

            // Create Service button
            var serviceButton = $('<button/>', {"class": "eventService ui-state-error",
                text: event.title + " [" + event.serviceName + "]"});

            // Set service icon
            setServiceIcon(serviceButton, 'ui-icon-newwin').click(function() {
                commandDialog(site, host.hostName, event.serviceName, event.commandId, event.title, event.exec);
            });

            // Set Tooltip
            serviceButton.tooltip({
                items: "[class]",
                content: function() {
                    var commandPre = $('<pre/>', {text: "=> " + event.hostName + " [" + event.serviceName + "]: " + event.exec});
                    var outputPre = $('<pre/>', {text: event.out});
                    return $('<div/>').append(commandPre, outputPre);
                }});

            // Add Button
            td.append(serviceButton);

            // Add row
            table.append(tr);

            // Found event
            jobEvents = true;
        });
    });

    // If no events
    if (!jobEvents) {
        var td1 = $('<td/>', {'colspan': 4}).append(LANG.noEvents);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // Remove Events
    var removeEventsButton = $('<button/>', {text: "Remove Events"});
    setButtonIcon(removeEventsButton).click(function() {
        $("#removeEventsSiteName").val(site.siteName);
        $("#removeEventsServerURL").val(site.serverURL);
        $("#removeEventsDialog").dialog("open");
    });

    // Return table
    return div.append(table, removeEventsButton);
}

function servicesTab(site) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui IP', text: 'IP'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Services'}));
    table.append(tr);
    div.append(table);

    // Check if no hosts
    if (!site.host.length) {
        var td1 = $('<td/>', {'colspan': 4}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each Host
    $.each(site.host, function() {
        var host = this;

        // Create a row
        var tr = $('<tr/>');
        tr.append($('<td/>', {'class': 'ui center', text: host.hostName + " [" + host.hostType + "]"}));
        tr.append($('<td/>', {'class': 'ui center', text: host.ip}));
        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        // For each service
        $.each(host.service, function() {
            var service = this;
            var serviceName = service.serviceName;

            // Create Service button
            var serviceButton = $('<button/>', {"class": "flowService", text: serviceName});

            // Set data
            serviceButton.data('serviceName', serviceName);
            serviceButton.data('hostName', host.hostName);

            // Set Service data as Host data
            service.hostName = host.hostName;
            service.ip = host.ip;

            // Set button onclic
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

            // Add Class
            if (!service.online) {
                if (serviceName === "OS") {
                    tr.addClass('ui-state-error');
                }
                serviceButton.addClass('ui-state-error');
            } else {
                serviceButton.addClass('ui-state-ok');
            }

            // Add service button to row
            td.append(serviceButton);
        });

        // Add row into table
        table.append(tr);
    });

    // Return Div
    return div;
}

function layoutTab(site) {
    var siteName = site.siteName;

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui IP', text: 'Type'}));
    tr.append($('<td/>', {'class': 'ui IP', text: 'IP'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Services'}));
    table.append(tr);

    // Check if no hosts
    if (!site.host.length) {
        var td1 = $('<td/>', {'colspan': 4}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each hosts
    $.each(site.host, function() {
        var host = this;
        var hostName = host.hostName;

        var tr = $('<tr/>');

        // Create host Button
        var hostButton = $('<button/>', {"class": "hostname", text: hostName});

        // Set Host Icon
        setHostIcon(hostButton, 'ui-icon-triangle-1-s').click(function() {

            // Create button menu
            var hostMenu = [];

            // Remove
            var removeHost = $('<a/>', {text: "Remove Host"}).click(function() {
                $('#menu').hide();
                $("#removeHostSiteName").val(site.siteName);
                $("#removeHostServerURL").val(site.serverURL);
                $("#removeHostName").val(hostName);
                $("#removeHostDialog").dialog("open");
            });
            removeHost.prepend($('<img/>', {'class': 'menu', src: '/server/img/alert.png'}));
            hostMenu.push($('<li/>').append(removeHost));

            // Rename
            var renameHost = $('<a/>', {text: "Rename Host"}).click(function() {
                $('#menu').hide();
                $("#renameHostSiteName").val(site.siteName);
                $("#renameHostServerURL").val(site.serverURL);
                $("#renameHostName").val(hostName);
                $("#renameHostDialog").dialog("open");
            });
            renameHost.prepend($('<img/>', {'class': 'menu', src: '/server/img/change.png'}));
            hostMenu.push($('<li/>').append(renameHost));

            // Update
            var updateHost = $('<a/>', {text: "Update Host"}).click(function() {
                $('#menu').hide();
                $("#updateHostSiteName").val(site.siteName);
                $("#updateHostServerURL").val(site.serverURL);
                $("#updateHostName").val(hostName);
                $("#updateHostIP").val(host.ip);
                $("#updateHostType").val(host.hostType).attr('selected', true);
                $("#updateHostDialog").dialog("open");
            });
            updateHost.prepend($('<img/>', {'class': 'menu', src: '/server/img/change.png'}));
            hostMenu.push($('<li/>').append(updateHost));

            $('#menu').empty();
            $('#menu').append(hostMenu);
            $('#menu').menu("refresh");
            $('#menu').show().position({
                my: "left top",
                at: "left bottom",
                of: this
            });
            return false;
        });

        // Create a row
        tr.append($('<td/>', {'class': 'ui'}).append(hostButton));
        tr.append($('<td/>', {'class': 'ui center', text: host.hostType}));
        tr.append($('<td/>', {'class': 'ui center', text: host.ip}));

        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        // For each service
        $.each(host.service, function() {
            var service = this;
            var serviceName = service.serviceName;

            // Create service Button
            var serviceButton = $('<button/>', {"class": "flowService", text: serviceName});

            // Existen Service
            if (service.online !== undefined && service.serviceName !== "OS") {

                // Set Onclick for Service button
                serviceButton.click(function() {

                    var url = "/jersey/Site/removeService";

                    // If Remote Site
                    if (site.serverURL) {
                        url = site.serverURL + url;
                    }

                    // AJAX
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: {SiteName: site.siteName, HostName: hostName, ServiceName: serviceName},
                        xhrFields: {
                            withCredentials: true
                        },
                        error: function() {
                            notificationMessage(LANG.submitError);
                        },
                        success: function(data) {
                            $('#A_' + site.siteName + '_LAYOUT').click();
                        }
                    });
                });
            } else {

                // For each command
                $.each(service.command, function() {
                    var command = this;

                    // Set Onclick for Service button
                    serviceButton.click(function() {
                        var dialog = commandDialog(site, host.hostName, serviceName, command.commandId, command.title, command.exec);

                        // Service OS
                        if (serviceName === "OS") {
                            return true;
                        }

                        // New Services
                        dialog.dialog("option", "buttons", {'Add Service': function() {
                                var url = "/jersey/Site/addService";

                                // If Remote Site
                                if (site.serverURL) {
                                    url = site.serverURL + url;
                                }

                                // AJAX
                                $.ajax({
                                    url: url,
                                    type: "POST",
                                    data: {SiteName: site.siteName, HostName: hostName, ServiceName: serviceName},
                                    xhrFields: {
                                        withCredentials: true
                                    },
                                    error: function() {
                                        notificationMessage(LANG.submitError);
                                    },
                                    success: function(data) {
                                        $('#A_' + site.siteName + '_LAYOUT').click();
                                    }
                                });
                                $(this).dialog("close");
                            }, Close: function() {
                                $(this).dialog("close");
                            }});
                    });
                });
            }

            // Set Icon
            if (serviceName === "OS") {
                setServiceIcon(serviceButton, "ui-icon-star");

                // Set Tooltip
                serviceButton.tooltip({
                    items: "[class]",
                    tooltipClass: "ui-tooltip-service",
                    content: "Operation System Service"});

            }
            else if (service.online !== undefined) {
                setServiceIcon(serviceButton, "ui-icon-close");

                // Set Tooltip
                serviceButton.tooltip({
                    items: "[class]",
                    tooltipClass: "ui-tooltip-service",
                    content: "Assigned Service. Click to manually Remove"});

            } else {
                setServiceIcon(serviceButton, "ui-icon-plus");

                // Set Tooltip
                serviceButton.tooltip({
                    items: "[class]",
                    tooltipClass: "ui-tooltip-service",
                    content: "Inactive Service. Click to manually Add"});
            }

            // Add Class
            if (service.online !== undefined) {
                if (!service.online) {
                    if (serviceName === "OS") {
                        tr.addClass('ui-state-error');
                    }
                    serviceButton.addClass('ui-state-error');
                } else {
                    serviceButton.addClass('ui-state-ok');
                }
            } else {
                serviceButton.addClass('ui-state-inactive');
            }

            // Add Service button
            td.append(serviceButton);
        });

        // Add row
        table.append(tr);
    });

    // Create buttons
    var addHostButton = $('<button/>', {text: "Add Host"});
    setButtonIcon(addHostButton).click(function() {
        $("#addHostSiteName").val(siteName);
        $("#addHostServerURL").val(site.serverURL);
        $("#addHostDialog").dialog("open");
    });

    // Return table
    return div.append(table, addHostButton);
}

function discoveryTab(configSite, job) {
    var siteName = configSite.siteName;

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Services'}));
    table.append(tr);

    // Check if running
    if (job.running) {
        var h1 = $('<h1/>', {'class': "center", text: 'Discovery is running...'});
        div.append(h1);
    } else if (job.finished) {
        var h1 = $('<h1/>', {'class': "center", text: 'Discovery finished at ' + job.finished});
        div.append(h1);
    }

    // Check if no hosts
    if (!job.host.length) {
        var td1 = $('<td/>', {'colspan': 2}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each hosts
    $.each(job.host, function() {
        var host = this;

        // Add row
        var tr = $('<tr/>');
        table.append(tr);

        // Create a row
        tr.append($('<td/>', {'class': 'ui center', text: host.hostName}));
        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        // For each service
        $.each(host.service, function() {
            var service = this;

            // For each command
            $.each(service.command, function() {
                var command = this;

                // Create command Button
                var commandButton = $('<button/>', {"class": "flowService", text: command.commandId + ' [' + service.serviceName + ']'});

                // Set Icon
                setCommandIcon(commandButton, 'ui-icon-newwin').click(function() {
                    commandDialog(configSite, host.hostName, service.serviceName, command.commandId, command.title, command.exec);
                });

                // Set Tooltip
                commandButton.tooltip({
                    items: "[class]",
                    content: function() {
                        var commandPre = $('<pre/>', {text: "=> " + host.hostName + " [" + service.serviceName + "]: " + command.exec});
                        var outputPre = $('<pre/>', {text: command.out});
                        return $('<div/>').append(commandPre, outputPre);
                    }});

                // Add Class
                if (command.error) {
                    commandButton.addClass('ui-state-error');
                } else {
                    commandButton.addClass('ui-state-ok');
                }

                // Add Command button
                td.append(commandButton);
            });
        });
    });

    // Create buttons
    var runDiscoveryButton = $('<button/>', {text: "Run Discovery"});
    setButtonIcon(runDiscoveryButton).click(function() {
        var url = "/jersey/Job/runJob";

        // If Remote Site
        if (configSite.serverURL) {
            url = configSite.serverURL + url;
        }

        // AJAX
        $.ajax({
            url: url,
            type: "POST",
            data: {SiteName: siteName, JobName: 'Discovery'},
            xhrFields: {
                withCredentials: true
            },
            error: function() {
                notificationMessage(LANG.submitError);
            },
            complete: function(data) {
                $('#A_' + siteName + '_DISCOVERY').click();
            }
        });
    });

    // Return table
    return div.append(table, runDiscoveryButton);
}

function jobTab(configSite, job) {
    var siteName = configSite.siteName;

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'Host'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Services'}));
    table.append(tr);

    // Check if running
    if (job.running) {
        var h1 = $('<h1/>', {'class': "center", text: job.jobName + ' job is running...'});
        div.append(h1);
    } else if (!job.enabled) {
        var h1 = $('<h1/>', {'class': "center", text: job.jobName + ' job is disabled...'});
        div.append(h1);
    } else if (job.finished) {
        var h1 = $('<h1/>', {'class': "center", text: job.jobName + ' job finished at ' + job.finished});
        div.append(h1);
    }

    // Check if no hosts
    if (!job.host.length) {
        var td1 = $('<td/>', {'colspan': 2}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
    }

    // For each hosts
    $.each(job.host, function() {
        var host = this;

        // Add row
        var tr = $('<tr/>');
        table.append(tr);

        // Create a row
        tr.append($('<td/>', {'class': 'ui center', text: host.hostName}));
        var td = $('<td/>', {'class': 'ui'});
        tr.append(td);

        // For each service
        $.each(host.service, function() {
            var service = this;

            // For each command
            $.each(service.command, function() {
                var command = this;

                // Create command Button
                var commandButton = $('<button/>', {"class": "flowService", text: command.commandId + ' [' + service.serviceName + ']'});

                // Set Icon
                setCommandIcon(commandButton, 'ui-icon-newwin').click(function() {
                    commandDialog(configSite, host.hostName, service.serviceName, command.commandId, command.title, command.exec);
                });

                // Set Tooltip
                commandButton.tooltip({
                    items: "[class]",
                    content: function() {
                        var commandPre = $('<pre/>', {text: "=> " + host.hostName + " [" + service.serviceName + "]: " + command.exec});
                        var outputPre = $('<pre/>', {text: command.out});
                        return $('<div/>').append(commandPre, outputPre);
                    }});

                // Add Class
                if (command.error) {
                    commandButton.addClass('ui-state-error');
                } else {
                    commandButton.addClass('ui-state-ok');
                }

                // Add Command button
                td.append(commandButton);
            });
        });
    });

    // Create buttons
    var runJobButton = $('<button/>', {text: "Run Job"});
    setButtonIcon(runJobButton).click(function() {
        var url = "/jersey/Job/runJob";

        // If Remote Site
        if (configSite.serverURL) {
            url = configSite.serverURL + url;
        }

        // AJAX
        $.ajax({
            url: url,
            type: "POST",
            data: {SiteName: siteName, JobName: job.jobName},
            xhrFields: {
                withCredentials: true
            },
            error: function() {
                notificationMessage(LANG.submitError);
            },
            complete: function(data) {
                $('#A_' + siteName + '_' + job.jobName).click();
            }
        });
    });

    var updateJobButton = $('<button/>', {text: "Disable Job"});

    // If Enabled
    if (!job.enabled) {
        updateJobButton.text("Enable Job");
    }

    setButtonIcon(updateJobButton).click(function() {
        var url = "/jersey/Job/updateJob";

        // If Remote Site
        if (configSite.serverURL) {
            url = configSite.serverURL + url;
        }

        // AJAX
        $.ajax({
            url: url,
            type: "POST",
            data: {SiteName: siteName, JobName: job.jobName, Enabled: !job.enabled},
            xhrFields: {
                withCredentials: true
            },
            error: function() {
                notificationMessage(LANG.submitError);
            },
            complete: function(data) {
                $('#A_' + siteName + '_' + job.jobName).click();
            }
        });
    });

    // Return table
    return div.append(table, runJobButton, updateJobButton);
}

function hostsTab(site) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Create a table
    var table = $('<table/>', {'class': 'ui-widget ui-widget-content'});
    var tr = $('<tr/>', {'class': "ui-widget-header"});
    tr.append($('<td/>', {'class': 'ui hostname', text: 'HostName'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Type'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'IP'}));
    tr.append($('<td/>', {'class': 'ui center', text: 'Info'}));
    table.append(tr);
    div.append(table);

    // Check if no hosts
    if (!site.host.length) {
        var td1 = $('<td/>', {'colspan': 2}).append(LANG.noHosts);
        table.append($('<tr/>', {'class': "ui"}).append(td1));
        return div;
    }

    // For each Host
    $.each(site.host, function() {
        var host = this;

        // Create host Button
        var hostButton = $('<button/>', {"class": "hostname", text: host.hostName});

        // Host row
        var tr = $('<tr/>', {'class': 'ui'});
        tr.append($('<td/>', {'class': 'ui hostname'}).append(hostButton));
        tr.append($('<td/>', {'class': 'ui hostname', text: host.hostType || ''}));
        tr.append($('<td/>', {'class': 'ui hostname', text: host.ip}));
        var infoTd = $('<td/>', {'class': 'ui', text: host.info || 'Already defined'});
        tr.append(infoTd);
        table.append(tr);

        // Host Already defined
        if (!host.info) {
            tr.addClass('ui-state-highlight');

            // Set Host Icon
            setHostIcon(hostButton, "ui-icon-circle-minus").click(function() {
                $("#removeHostSiteName").val(site.siteName);
                $("#removeHostServerURL").val(site.serverURL);
                $("#removeHostName").val(host.hostName);
                $("#removeHostDialog").dialog("open");

                infoTd.text('Removed');
                tr.removeClass('ui-state-highlight');
            });
        } else {

            // Set Host Icon
            setHostIcon(hostButton, "ui-icon-circle-plus").click(function() {
                $("#addHostSiteName").val(site.siteName);
                $("#addHostServerURL").val(site.serverURL);
                $("#addHostName").val(host.hostName);
                $("#addHostIP").val(host.ip);
                $("#addHostType").val(host.hostType);
                $("#addHostDialog").dialog("open");

                infoTd.text('Added');
                tr.addClass('ui-state-highlight');
            });
        }
    });

    // Return Div
    return div;
}
