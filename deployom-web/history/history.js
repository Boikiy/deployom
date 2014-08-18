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

    // Set history
    $('#history').button({icons: {primary: "deployom-database"}}).click(function(event) {
        location.replace('/history');
    });

    // For tabs
    var ul = $('<ul/>');

    // Add Event tab
    var eventLi = $('<li/>');
    var a = $('<a/>', {id: 'A_EVENTS', text: "Events History", href: '#EVENTS'});
    eventLi.append(a);
    ul.append(eventLi);
    $('#tabsDiv').append($('<div/>', {id: 'EVENTS'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/History/getEvents",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#EVENTS').empty();
                $('#EVENTS').prepend(LANG.loading);
            },
            error: function() {
                $('#EVENTS').empty();
                $('#EVENTS').prepend(LANG.submitError);
            },
            success: function(log) {
                $('#EVENTS').empty();
                $('#EVENTS').append(eventsTab(log));
            }
        });
    });

    // Show
    a.click();

    // Add Event tab
    var eventLi = $('<li/>');
    var a = $('<a/>', {id: 'A_COMMANDS', text: "Operations History", href: '#COMMANDS'});
    eventLi.append(a);
    ul.append(eventLi);
    $('#tabsDiv').append($('<div/>', {id: 'COMMANDS'}));

    a.click(function(event) {
        $.ajax({
            url: "/jersey/History/getCommands",
            type: "GET",
            dataType: "json",
            beforeSend: function(data) {
                $('#COMMANDS').empty();
                $('#COMMANDS').prepend(LANG.loading);
            },
            error: function() {
                $('#COMMANDS').empty();
                $('#COMMANDS').prepend(LANG.submitError);
            },
            success: function(log) {
                $('#COMMANDS').empty();
                $('#COMMANDS').append(commandsTab(log));
            }
        });
    });

    // Return
    return ul;
}

function eventsTab(log) {
    var div = $('<div/>');

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'finished', text: 'Date Time'});
    var td2 = $('<td/>', {'class': 'hostname', text: 'Site'});
    var td3 = $('<td/>', {'class': 'hostname', text: 'Host'});
    var td4 = $('<td/>', {'class': 'hostname', text: 'Service'});
    var td5 = $('<td/>', {text: 'Command'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5));

    // If no events defined
    if (!log.event.length) {
        var td1 = $('<td/>', {'colspan': 5}).append(LANG.noEvents);
        table.append($('<tr/>').append(td1));
    }

    // For each event
    $.each(log.event, function() {
        var event = this;

        // Data columns
        var td1 = $('<td/>', {text: event.datetime});
        var td2 = $('<td/>', {text: event.siteName});
        var td3 = $('<td/>', {text: event.hostName});
        var td4 = $('<td/>', {text: event.serviceName});
        var td5 = $('<td/>', {text: event.title});

        // Add row
        var tr = $('<tr/>', {'class': "high"}).append(td1, td2, td3, td4, td5);
        table.append(tr);

        // Set Tooltip
        tr.tooltip({
            items: "[class]",
            content: function() {
                var commandPre = $('<pre/>', {text: "=> " + event.hostName + " [" + event.serviceName + "]: " + event.exec});
                var outputPre = $('<pre/>', {text: event.out});
                return $('<div/>').append(commandPre, outputPre);
            }});
    });

    // Return table
    return div.append(table);
}

function commandsTab(log) {
    var div = $('<div/>');

    // Create a table
    var table = $('<table/>', {'class': "ui-widget ui-widget-content"});
    var td1 = $('<td/>', {'class': 'finished', text: 'Date Time'});
    var td2 = $('<td/>', {'class': 'hostname', text: 'Who'});
    var td3 = $('<td/>', {'class': 'hostname', text: 'Site'});
    var td4 = $('<td/>', {'class': 'hostname', text: 'Host'});
    var td5 = $('<td/>', {'class': 'hostname', text: 'Service'});
    var td6 = $('<td/>', {text: 'Command'});
    table.append($('<tr/>', {'class': "ui-widget-header"}).append(td1, td2, td3, td4, td5, td6));

    // If no events defined
    if (!log.command.length) {
        var td1 = $('<td/>', {'colspan': 6}).append(LANG.noOperations);
        table.append($('<tr/>').append(td1));
    }

    // For each commands
    $.each(log.command, function() {
        var command = this;

        // Data columns
        var td1 = $('<td/>', {text: command.datetime});
        var td2 = $('<td/>', {text: command.userName});
        var td3 = $('<td/>', {text: command.siteName});
        var td4 = $('<td/>', {text: command.hostName});
        var td5 = $('<td/>', {text: command.serviceName});
        var td6 = $('<td/>', {text: command.title});

        // Add row
        var tr = $('<tr/>', {'class': "high"}).append(td1, td2, td3, td4, td5, td6);
        table.append(tr);

        // Set Tooltip
        tr.tooltip({
            items: "[class]",
            content: function() {
                var commandPre = $('<pre/>', {text: "=> " + command.hostName + " [" + command.serviceName + "]: " + command.exec});
                var outputPre = $('<pre/>', {text: command.out});
                return $('<div/>').append(commandPre, outputPre);
            }});
    });

    // Return table
    return div.append(table);
}
