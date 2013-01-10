Ext.define('Admin.view.homescreen.Homescreen', {
    extend: 'Ext.container.Container',
    alias: 'widget.homescreen',
    requires: [
        'Admin.view.homescreen.LoginPanel',
        'Admin.view.homescreen.AppSelector'
    ],

    userIsLoggedIn: false,

    id: 'admin-home-main-container',

    autoEl: {
        tag: 'div',
        cls: 'clickable',
        html: Templates.homescreen.mainContainerHtml
    },
    floating: true,
    hideMode: 'offsets',
    renderTo: Ext.getBody(),

    initComponent: function () {
        var me = this;

        me.on('render', function () {
            me.updateGlobalView();
            me.addClickEvent();

            Ext.create('Admin.view.homescreen.LoginPanel');
            Ext.create('Admin.view.homescreen.AppSelector');

            if (me.userIsLoggedIn) {
                me.displayAppSelector();
            } else {
                me.displayLogin();
            }
        });

        me.callParent(arguments);
    },


    displayLogin: function () {
        var me = this,
            loginElements = Ext.DomQuery.select('div[data-screen="login"]'),
            appSelectorElements = Ext.DomQuery.select('div[data-screen="app-selector"]');

        Ext.Array.forEach(appSelectorElements, function (el) {
            Ext.fly(el).setStyle('display', 'none');
        });

        Ext.Array.forEach(loginElements, function (el) {
            Ext.fly(el).setStyle('display', 'block');
        });
    },


    displayAppSelector: function () {
        var me = this,
            loginElements = Ext.DomQuery.select('div[data-screen="login"]'),
            appSelectorElements = Ext.DomQuery.select('div[data-screen="app-selector"]'),
            appSelectorContainer = Ext.get('admin-home-app-selector');

        Ext.Array.forEach(loginElements, function (el) {
            Ext.fly(el).setStyle('display', 'none');
        });

        Ext.Array.forEach(appSelectorElements, function (el) {
            Ext.fly(el).setStyle('display', 'block');
        });

        Ext.getCmp('admin-home-app-selector-search').focus();

        appSelectorContainer.addCls('fade-in');
    },


    updateGlobalView: function () {
        var me = this;

        me.setBackgroundImage('resources/images/x_Aerial-View-of-the-Island-of-Bora-Bora-French-Polynesia.jpg');
        me.setVersionText('5.0.1 Enterprise Edition');
        me.setInstallationLabelText('Production');
        me.setLicensedToText('Licensed to Large Customer');
    },


    addClickEvent: function () {
        var me = this;
        Ext.fly('admin-home-main-container').on('click', function (evt, el) {
            var hide = el.className && el.className.indexOf('clickable') > -1;
            if (hide) {
                me.toggleShowHide();
            }
        }, me);
    },


    toggleShowHide: function () {
        var me = this;
        me.setVisible(!me.isVisible());
    },


    getLoginFormPanel: function () {
        return Ext.ComponentQuery.query('loginPanel')[0];
    },


    getAppSelectorContainer: function () {
        return Ext.ComponentQuery.query('appSelector')[0];
    },


    setBackgroundImage: function (imagePath) {
        Ext.fly('admin-home-main-container').setStyle('background-image', 'url(' + imagePath + ')');
    },


    setVersionText: function (text) {
        Ext.fly('admin-home-version-info').setHTML(text);
    },


    setInstallationLabelText: function (text) {
        Ext.fly('admin-home-installation-info').setHTML(' | ' + text);
    },


    setLicensedToText: function (text) {
        Ext.fly('admin-home-login-licensed-to').setHTML(text);
    }

});
