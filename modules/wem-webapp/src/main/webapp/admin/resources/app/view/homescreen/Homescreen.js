Ext.define('Admin.view.homescreen.Homescreen', {
    extend: 'Ext.Component',
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
    hideMode: 'display',
    renderTo: Ext.getBody(),
    shadow: false,

    initComponent: function () {
        var me = this;

        me.on('render', function () {
            me.updateGlobalView();
            me.addClickEvent();
            me.prefixedEvent(me.getEl().dom, 'AnimationEnd', function (evt) {
                if (evt.animationName === 'ani-hide-home') {
                    me.setVisible(false);
                }
            });

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
        me.setBackgroundImage('background.jsp');
        me.setLicensedToText('Licensed to Large Customer');
    },


    toggleShowHide: function () {
        var me = this;
        var el = me.getEl();
        var show = !me.isVisible();

        if (show) {
            me.setVisible(true);
            el.removeCls('hide-home').addCls('show-home');
        } else {
            el.removeCls('show-home').addCls('hide-home');
        }
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


    prefixedEvent: function (element, type, callback) {
        var pfx = ['webkit', 'moz', 'MS', 'o', ''];
        var i;

        for (i = 0; i < pfx.length; i++) {
            if (!pfx[i]) {
                type = type.toLowerCase();
            }

            element.addEventListener(pfx[i] + type, callback, false);
        }
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
