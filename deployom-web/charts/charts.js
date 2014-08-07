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

    // Set charts
    $('#charts').button({icons: {primary: "deployom-charts"}}).click(function(event) {
        location.replace('/charts');
    });

    // If no sites
    if (!config.site.length) {
        return $('<div/>', {text: LANG.noSites});
    }

    // Site Tabs
    var ul = $('<ul/>');

    $.each(config.site, function() {
        var configSite = this;

        // Add Site tab
        var siteLi = $('<li/>');
        var a = $('<a/>', {id: 'A_' + configSite.siteName, text: configSite.siteName, href: '#' + configSite.siteName, title: 'Click to Refresh'});
        siteLi.append(a);
        ul.append(siteLi);

        // If updating disabled
        if (!configSite.enabled) {
            a.append(' [DISABLED]');
        }

        $("#tabsDiv").append($('<div/>', {id: configSite.siteName}));

        // Hide menu
        a.click(function() {
            $('#menu').hide();

            var url = "/jersey/Site/getCharts";
            if (configSite.serverURL) {
                url = configSite.serverURL + url;
            }

            // Request Chart
            $.ajax({
                url: url,
                type: "POST",
                data: {SiteName: configSite.siteName},
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
                    $('#' + configSite.siteName).prepend(chartsTab(site));
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

function chartsTab(site) {

    var div = $('<div/>').click(function() {
        $('#menu').hide();
    });

    // Site Header
    var h1 = $('<h1/>', {'class': "center", text: site.siteName});
    div.append(h1);

    // Create columns div
    var col1Div = $('<div/>', {'class': 'column'});
    var col2Div = $('<div/>', {'class': 'column'});
    div.append(col1Div, col2Div);

    // Add Chart Button
    var addChartButton = $('<button/>', {"class": "hostname", text: 'Add Chart'});
    setButtonIcon(addChartButton, "ui-icon-triangle-1-s");

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'ui hostname'});
    var td2 = $('<td/>', {'class': 'ui'});
    table.append($('<tr/>').append(td1, td2));
    div.append(table);

    // Reload Interval
    var reloadDiv = $('<div/>', {'class': 'floatLeft', 'text': 'Reload Interval (seconds) '});

    // Create time intervals
    var radioName = 'reload' + site.siteName;
    var input1 = $('<input/>', {'type': 'radio', 'name': radioName, id: radioName + '1', value: 30000});
    var label1 = $('<label/>', {'for': radioName + '1', text: '30'});
    var input2 = $('<input/>', {'type': 'radio', 'name': radioName, id: radioName + '2', value: 60000});
    var label2 = $('<label/>', {'for': radioName + '2', text: '60'});
    var input3 = $('<input/>', {'type': 'radio', 'name': radioName, id: radioName + '3', value: 300000, checked: 'checked'});
    var label3 = $('<label/>', {'for': radioName + '3', text: '300'});
    var input4 = $('<input/>', {'type': 'radio', 'name': radioName, id: radioName + '4', value: 600000});
    var label4 = $('<label/>', {'for': radioName + '4', text: '600'});
    reloadDiv.append(input1, label1, input2, label2, input3, label3, input4, label4);
    reloadDiv.buttonset();
    td1.append(addChartButton);
    td2.append(reloadDiv);

    // Chart Id
    var chartNo = 1;

    // Set onlick
    addChartButton.click(function() {

        var servicesMenu = [];

        // For each Host in Site
        $.each(site.host, function() {
            var host = this;

            // For each service
            $.each(host.service, function() {
                var service = this;

                var chartsMenu = $('<ul/>');

                // If any charts
                if (!service.chart.length) {
                    return true;
                }

                // For each Chart
                $.each(service.chart, function() {
                    var chart = this;
                    var title = chart.title;

                    // Add a button
                    var li = $('<li/>', {text: title});

                    // Set Service Icon
                    setMenuImage(title, li);

                    // Set onclick
                    li.click(function() {
                        $('#menu').hide();

                        // Adding Portlet
                        var portlet = $('<div/>', {'class': 'portlet ui-widget ui-widget-content ui-helper-clearfix ui-corner-all'});
                        var portletHeader = $('<div/>', {'class': 'portlet-header ui-widget-header ui-corner-all',
                            text: title + ' [' + service.serviceName + ']'});
                        var portletContent = $('<div/>', {'class': 'left'});
                        var chartDiv = $('<div/>', {'class': 'chart4'});
                        portletContent.append(chartDiv);
                        portlet.append(portletHeader, portletContent);
                        if (chartNo % 2 === 1) {
                            col1Div.append(portlet);
                        } else {
                            col2Div.append(portlet);
                        }

                        // Add Reload button
                        var reloadButton = $('<button/>', {text: "Reload" + ' (' + reloadDiv.find(':radio:checked').val() / 1000 + ' seconds)'});
                        setButtonIcon(reloadButton).click(function() {

                            var url = "/jersey/Host/getChart";

                            // If Remote Site
                            if (site.serverURL) {
                                url = site.serverURL + url;
                            }

                            // AJAX
                            $.ajax({
                                url: url,
                                type: "POST",
                                dataType: "json",
                                data: {SiteName: site.siteName, ServiceName: service.serviceName,
                                    HostName: host.hostName, CommandId: chart.chartId},
                                xhrFields: {
                                    withCredentials: true
                                },
                                beforeSend: function() {
                                    chartDiv.text(LANG.loading);
                                },
                                success: function(chart) {

                                    var lines = [];
                                    var series = [];

                                    // Adding Lines and Labels
                                    for (var i = 1; i <= 3; i++) {
                                        series.push({label: chart['label' + i]});
                                        lines.push(chart['line' + i]);
                                    }

                                    chartDiv.empty();
                                    chartDiv.jqplot(lines, {
                                        seriesColors: ['#73C774', '#C7754C', '#EAA228'],
                                        title: title,
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
                        });

                        // Add Remove button
                        var removeButton = $('<button/>', {text: "Remove"});
                        setButtonIcon(removeButton).click(function() {
                            chartNo--;
                            portlet.remove();
                        });

                        portletContent.append(reloadButton, removeButton);

                        // Make portlets sortable
                        $(".column").sortable({
                            connectWith: ".column",
                            handle: ".portlet-header",
                            placeholder: "portlet-placeholder ui-corner-all"
                        });

                        // Increasing Chart
                        chartNo++;

                        // Set Reload Interval
                        setInterval(function() {
                            // Reload chart
                            reloadButton.click();
                        }, reloadDiv.find(':radio:checked').val());

                        // Click to show Chart
                        reloadButton.click();
                    });

                    chartsMenu.append(li);
                });

                // Add Service Group
                servicesMenu.push($('<li/>', {text: service.serviceName + ' [' + host.hostName + ']'}).append(chartsMenu));
            });
        });

        // Sort Services
        servicesMenu.sort(function(a, b) {
            return $(a).text().toUpperCase().localeCompare($(b).text().toUpperCase());
        });

        // Menu
        showMenu(servicesMenu, this);
        return false;
    });

    // Return Div
    return div;
}