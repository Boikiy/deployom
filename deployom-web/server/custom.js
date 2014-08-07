function setMenuImage(title, li) {
    if (title.match(/Online|State/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/online.png'}));
    }
    else if (title.match(/Start/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/start.png'}));
    }
    else if (title.match(/Stop/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/alert.png'}));
    }
    else if (title.match(/Console/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/web.png'}));
    }
    else if (title.match(/CSR|CSS|CRM|User/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/customer.png'}));
    }
    else if (title.match(/Manager|OAM/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/manager.png'}));
    }
    else if (title.match(/Security/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/security.png'}));
    }
    else if (title.match(/Usage|Statistics|Request|Order/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/charts.png'}));
    }
    else if (title.match(/Log/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/config.png'}));
    }
    else if (title.match(/List|Display/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/list.png'}));
    }
    else if (title.match(/Trace/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/trace.png'}));
    }
    else if (title.match(/Remove/i)) {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/alert.png'}));
    }
    else {
        li.prepend($('<img/>', {'class': 'menu', src: '/server/img/other.png'}));
    }
}

function setHostIcon(button, secondary) {

    // Get Button Text
    var name = button.text();

    if (!name) {
        button.button({icons: {secondary: secondary}});
        return button;
    }

    if (name.match(/\S+DB|^ADMIN|MASTER|SLAVE/i)) {
        button.button({icons: {primary: "deployom-database", secondary: secondary}});
    } else if (name.match(/UPM/i)) {
        button.button({icons: {primary: "deployom-oam", secondary: secondary}});
    } else if (name.match(/SECSERV/i)) {
        button.button({icons: {primary: "deployom-security", secondary: secondary}});
    } else if (name.match(/BILLING/i)) {
        button.button({icons: {primary: "deployom-billing", secondary: secondary}});
    } else if (name.match(/CSS|CRM|INFOR/i)) {
        button.button({icons: {primary: "deployom-customer", secondary: secondary}});
    } else if (name.match(/SAPI|ASYNCH|FE_ADMIN|RHT/i)) {
        button.button({icons: {primary: "deployom-frontend", secondary: secondary}});
    } else if (name.match(/CMS|ASU|UGS/i)) {
        button.button({icons: {primary: "deployom-interface", secondary: secondary}});
    } else if (name.match(/VIOS|CISCO|JUNIPER/i)) {
        button.button({icons: {primary: "deployom-virtual", secondary: secondary}});
    } else if (name.match(/SLU|SGU|DGU|URE/i)) {
        button.button({icons: {primary: "deployom-rating", secondary: secondary}});
    } else if (name.match(/OFR|ORP/i)) {
        button.button({icons: {primary: "deployom-file", secondary: secondary}});
    } else if (name.match(/ME|SUBEX/i)) {
        button.button({icons: {primary: "deployom-mediation", secondary: secondary}});
    } else {
        button.button({icons: {secondary: secondary}});
    }

    return button;
}

function setCommandIcon(button, secondary) {

    // Get Button Text
    var name = button.text();

    if (name.match(/Online|State/i)) {
        button.button({icons: {primary: "deployom-online", secondary: secondary}});
    } else if (name.match(/Monitor/i)) {
        button.button({icons: {primary: "deployom-virtual", secondary: secondary}});
    } else if (name.match(/Start/i)) {
        button.button({icons: {primary: "deployom-start", secondary: secondary}});
    } else if (name.match(/Stop/i)) {
        button.button({icons: {primary: "deployom-alert", secondary: secondary}});
    } else if (name.match(/Log/i)) {
        button.button({icons: {primary: "deployom-config", secondary: secondary}});
    } else if (name.match(/Time|Disk|Swap/i)) {
        button.button({icons: {primary: "deployom-os", secondary: secondary}});
    } else if (name.match(/Events/i)) {
        button.button({icons: {primary: "deployom-oam", secondary: secondary}});
    } else if (name.match(/Alert|Table|Constraint/i)) {
        button.button({icons: {primary: "deployom-database", secondary: secondary}});
    } else if (name.match(/Cache|CRU/i)) {
        button.button({icons: {primary: "deployom-storage", secondary: secondary}});
    } else {
        button.button({icons: {secondary: secondary}});
    }

    return button;
}

function setServiceIcon(button, secondary) {

    // Get Button Text
    var name = button.text();

    if (name.match(/\S+DB|Database|^XEDB|OAMDB|Oracle/i)) {
        button.button({icons: {primary: "deployom-database", secondary: secondary}});
    } else if (name.match(/MANAGER|UPA|UPM|OAM/i)) {
        button.button({icons: {primary: "deployom-oam", secondary: secondary}});
    } else if (name.match(/SECSERV|Security/i)) {
        button.button({icons: {primary: "deployom-security", secondary: secondary}});
    } else if (name.match(/BIP|IGEN|Billing/i)) {
        button.button({icons: {primary: "deployom-billing", secondary: secondary}});
    } else if (name.match(/RCB|^RCS|Charging/i)) {
        button.button({icons: {primary: "deployom-charging", secondary: secondary}});
    } else if (name.match(/^PURGE/i)) {
        button.button({icons: {primary: "deployom-flow", secondary: secondary}});
    } else if (name.match(/^HTTP|FEADMIN|LDAP|Application|Session/i)) {
        button.button({icons: {primary: "deployom-http", secondary: secondary}});
    } else if (name.match(/^BACKUP/i)) {
        button.button({icons: {primary: "deployom-backup", secondary: secondary}});
    } else if (name.match(/ASU|RHT|SAPI|Order|Frontend/i)) {
        button.button({icons: {primary: "deployom-frontend", secondary: secondary}});
    } else if (name.match(/TSP|Propagation/i)) {
        button.button({icons: {primary: "deployom-propagation", secondary: secondary}});
    } else if (name.match(/CSS|INFOR|Customer|ThirdParty/i)) {
        button.button({icons: {primary: "deployom-customer", secondary: secondary}});
    } else if (name.match(/^COM|CMCAP|LTP|File|Transfer/i)) {
        button.button({icons: {primary: "deployom-file", secondary: secondary}});
    } else if (name.match(/^Discovery|Performance/i)) {
        button.button({icons: {primary: "deployom-discovery", secondary: secondary}});
    } else if (name.match(/lbb|pps./i)) {
        button.button({icons: {primary: "deployom-config", secondary: secondary}});
    } else if (name.match(/NOTIF|Interface|SMS|IVR/i)) {
        button.button({icons: {primary: "deployom-interface", secondary: secondary}});
    } else if (name.match(/INFOR_OM|Usage|Management|Utilization/i)) {
        button.button({icons: {primary: "deployom-management", secondary: secondary}});
    } else if (name.match(/OMNI|Rating|Voice|Diameter|OPPS|TPPS|CCAP|OCS_SLF/i)) {
        button.button({icons: {primary: "deployom-rating", secondary: secondary}});
    } else if (name.match(/CLUSTER|Virtual|Hardware|Platform|LPAR/i)) {
        button.button({icons: {primary: "deployom-virtual", secondary: secondary}});
    } else if (name.match(/Asynch|Workflow|Job/i)) {
        button.button({icons: {primary: "deployom-workflow", secondary: secondary}});
    } else if (name.match(/Web/i)) {
        button.button({icons: {primary: "deployom-web", secondary: secondary}});
    } else if (name.match(/^OS/i)) {
        button.button({icons: {primary: "deployom-os", secondary: secondary}});
    } else if (name.match(/EMC/i)) {
        button.button({icons: {primary: "deployom-storage", secondary: secondary}});
    } else if (name.match(/Mail/i)) {
        button.button({icons: {primary: "deployom-mail", secondary: secondary}});
    } else if (name.match(/^SERVER|Mediation/i)) {
        button.button({icons: {primary: "deployom-mediation", secondary: secondary}});
    } else {
        button.button({icons: {secondary: secondary}});
    }

    return button;
}

function setSiteIcon(button, secondary) {

    // Get Button Text
    var name = button.text();

    if (!secondary) {
        secondary = "ui-icon-triangle-1-s";
    }

    // Set Site Button
    if (name.match(/prod/i)) {
        button.button({icons: {primary: "deployom-prod", secondary: secondary}});
    } else if (name.match(/test/i)) {
        button.button({icons: {primary: "deployom-test", secondary: secondary}});
    } else {
        button.button({icons: {primary: "deployom-local", secondary: secondary}});
    }

    return button;
}

function setUserIcon(role, button, secondary) {

    if (!secondary) {
        secondary = "ui-icon-triangle-1-s";
    }

    // Set User Button
    if (role.match(/admin/i)) {
        button.button({icons: {primary: "deployom-prod", secondary: secondary}});
    } else if (role.match(/support/i)) {
        button.button({icons: {primary: "deployom-test", secondary: secondary}});
    } else {
        button.button({icons: {primary: "deployom-local", secondary: secondary}});
    }

    return button;
}

function setButtonIcon(button, secondary) {

    // Get Button Text
    var name = button.text();

    if (name.match(/Add|\+/i)) {
        button.button({icons: {primary: "ui-icon-circle-plus", secondary: secondary}});
    } else if (name.match(/Stop|Enable|Disable/i)) {
        button.button({icons: {primary: "ui-icon-power", secondary: secondary}});
    } else if (name.match(/Remove/i)) {
        button.button({icons: {primary: "ui-icon-trash", secondary: secondary}});
    } else if (name.match(/Update|Replace/i)) {
        button.button({icons: {primary: "ui-icon-pencil", secondary: secondary}});
    } else if (name.match(/Rename/i)) {
        button.button({icons: {primary: "ui-icon-gear", secondary: secondary}});
    } else if (name.match(/Download/i)) {
        button.button({icons: {primary: "ui-icon-copy", secondary: secondary}});
    } else if (name.match(/Upload/i)) {
        button.button({icons: {primary: "ui-icon-folder-open", secondary: secondary}});
    } else if (name.match(/Reload/i)) {
        button.button({icons: {primary: "ui-icon-refresh", secondary: secondary}});
    } else if (name.match(/Discovery/i)) {
        button.button({icons: {primary: "ui-icon-search", secondary: secondary}});
    } else if (name.match(/Job/i)) {
        button.button({icons: {primary: "ui-icon-clock", secondary: secondary}});
    } else {
        button.button({icons: {secondary: secondary}});
    }

    return button;
}

function setFlowImage(button) {

    // Get Button Text
    var name = button.text();

    // Button
    button.button();

    if (name.match(/workflow|job/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/workflow.png'}));
    }
    else if (name.match(/oam/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/oam.png'}));
    }
    else if (name.match(/propagation/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/propagation.png'}));
    }
    else if (name.match(/virtual|platform/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/virtual.png'}));
    }
    else if (name.match(/interface|ivr/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/interface.png'}));
    }
    else if (name.match(/database/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/database.png'}));
    }
    else if (name.match(/rating/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/rating.png'}));
    }
    else if (name.match(/customer/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/customer.png'}));
    }
    else if (name.match(/billing/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/billing.png'}));
    }
    else if (name.match(/management/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/charts.png'}));
    }
    else if (name.match(/file/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/file.png'}));
    }
    else if (name.match(/frontend/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/frontend.png'}));
    }
    else {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/other.png'}));
    }

    return button;
}

function setSiteImage(button) {

    // Get Button Text
    var name = button.text();

    // Button
    button.button();

    if (name.match(/prod/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/prod.png'}));
    } else if (name.match(/test/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/test.png'}));
    } else {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/local.png'}));
    }

    return button;
}


function setModuleImage(button) {

    // Get Button Text
    var name = button.text();

    // Button
    button.button();

    if (name.match(/audit/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/billing.png'}));
    }
    else if (name.match(/dashboard/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/dashboard.png'}));
    }
    else if (name.match(/charts/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/charts.png'}));
    }
    else if (name.match(/designer/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/designer.png'}));
    }
    else if (name.match(/history/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/database.png'}));
    }
    else if (name.match(/help/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/help.png'}));
    }
    else if (name.match(/server/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/server.png'}));
    }
    else if (name.match(/mail/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/mail.png'}));
    }
    else if (name.match(/add|new/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/add.png'}));
    }
    else if (name.match(/remote/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/virtual.png'}));
    }
    else if (name.match(/purge/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/flow.png'}));
    }
    else if (name.match(/logout/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/security.png'}));
    }
    else if (name.match(/weblogic/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/web.png'}));
    }
    else if (name.match(/oracle/i)) {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/database.png'}));
    }
    else {
        button.prepend($('<img/>', {'class': 'flow', src: '/server/img/module.png'}));
    }

    return button;
}
