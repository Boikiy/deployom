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

    // Set audit
    $('#audit').button({icons: {primary: "deployom-billing"}}).click(function(event) {
        location.replace('/audit');
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
                    $('#' + configSite.siteName).empty();
                    $('#' + configSite.siteName).append(LANG.loading);
                },
                success: function(site) {
                    $('#' + configSite.siteName).empty();
                    site.serverURL = configSite.serverURL;
                    $('#' + configSite.siteName).append(auditTab(site));
                },
                error: function() {
                    $('#' + configSite.siteName).empty();
                    $('#' + configSite.siteName).append(LANG.submitError);
                }
            });
        });
    });

    // Return
    return ul;
}

function auditTab(site) {
    var siteName = site.siteName;

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Add Site Header
    var h1 = $('<h1/>', {'class': "center", text: siteName});
    div.append(h1);

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname', text: 'Job'});
    var td2 = $('<td/>', {'class': 'ui hostname', text: 'Periodic'});
    var td3 = $('<td/>', {'class': 'ui', text: 'Finished'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3));
    div.append(table);

    // Check all Jobs
    $.each(site.job, function() {
        var job = this;

        var jobButton = $('<button/>', {'class': 'hostname', text: job.jobName, title: 'Click to Run'});
        setServiceIcon(jobButton, "ui-icon-clock").click(function() {
            var url = "/jersey/Job/runJob";

            // If Remote Site
            if (site.serverURL) {
                url = site.serverURL + url;
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
                success: function(data) {
                    notificationMessage(data);
                }
            });
        });

        // Data columns
        var td1 = $('<td/>', {'class': 'ui'}).append(jobButton);
        var td2 = $('<td/>', {'class': 'ui', text: 'On Demand'});
        var td3 = $('<td/>', {'class': 'ui', text: job.finished});

        // Add row
        var tr = $('<tr/>', {'class': "ui"}).append(td1, td2, td3);
        table.append(tr);

        // If disabled
        if (!job.enabled) {
            td3.text('DISABLED');
        }

        // If scheduled
        if (job.period && job.start) {
            td2.text('Periodic (' + job.period + ' minutes)');
        }

        // If running
        if (job.running) {
            td3.text('RUNNING');
            jobButton.button('disable');
        }
        else if (!job.finished) {
            td2.text('');
            td3.text('No Results');
            tr.addClass('ui-state-highlight');
        }
    });

    var downloadButton = $('<button/>', {text: "Download Excel Report"});
    setButtonIcon(downloadButton).click(function() {
        var url = "/jersey/Audit/downloadAudit";

        // If Remote Site
        if (site.serverURL) {
            url = site.serverURL + url;
        }

        // Download Form
        var form = $('<form/>', {method: "post", action: url, 'class': 'hidden'});
        div.append(form);
        form.append($('<input/>', {name: "SiteName", value: siteName}));
        form.submit();
    });

    // Return
    return div.append(downloadButton);
}
