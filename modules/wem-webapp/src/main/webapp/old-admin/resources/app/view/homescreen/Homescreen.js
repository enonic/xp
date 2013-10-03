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
        html:
            '<div id="admin-home-branding">' +
                '<div id="admin-home-installation-info"><!-- --></div>' +
                '<div id="admin-home-version-info"><!-- --></div>' +
            '</div>' +
            '<div id="admin-home-center">' +
                '<div id="admin-home-left-column">' +
                    '<div data-screen="login" style="display: none">' +
                    '</div>' +
                    '<div data-screen="app-selector" style="display: none">' +
                        '<div id="admin-home-app-selector">' +
                            '<div id="admin-home-app-selector-search-input-container"></div>' +
                            '<div id="admin-home-app-tiles-placeholder">' +
                                '<!-- -->' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div id="admin-home-right-column">' +
                    '<div data-screen="login" style="display: none">' +
                        '<div id="admin-home-login-form">' +
                            '<div id="admin-home-login-form-container"></div>' +
                            '<div id="admin-home-login-licensed-to"><!-- --></div>' +
                        '</div>' +
                    '</div>' +
                    '<div data-screen="app-selector" style="display: none">' +
                        '<div id="admin-home-app-info-container">' +
                            '<h3 id="admin-home-app-info-name"><!-- --></h3>' +
                            '<div id="admin-home-app-info-description"><!-- --></div>' +
                        '</div>' +
                    '</div>' +
                    '<div id="admin-home-links-container">' +
                        '<a href="http://www.enonic.com/community">Community</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.enonic.com/docs">Documentation</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://enonic.com/en/home/enonic-cms">About</a>' +
                    '</div>' +
                '</div>' +
            '</div>'
    },
    floating: true,
    hideMode: 'display',
    renderTo: Ext.getBody(),
    shadow: false,

    initComponent: function () {
        var me = this;

        me.on('render', function () {
            me.prefixedEvent(me.getEl().dom, 'AnimationEnd', function (evt) {
                if (evt.animationName === 'animation-hide-home') {
                    me.setVisible(false);
                }
            });

            Ext.create('Admin.view.homescreen.LoginPanel');
            Ext.create('Admin.view.homescreen.AppSelector');
        });

        me.callParent(arguments);
    },


    toggleShowHide: function () {
        var me = this,
            el = me.getEl(),
            show = !me.isVisible();

        if (show) {
            me.showScreen();
        } else {
            me.hideScreen();
        }
    },


    showScreen: function () {
        this.setVisible(true);
        this.getEl().removeCls('hide-home').addCls('show-home');
    },

    hideScreen: function () {
        this.getEl().removeCls('show-home').addCls('hide-home');
    },


    /* Refactor to a helper? */
    prefixedEvent: function (element, type, callback) {
        var prefixes = ['webkit', 'moz', 'MS', 'o', ''];
        var i;

        for (i = 0; i < prefixes.length; i++) {
            if (!prefixes[i]) {
                type = type.toLowerCase();
            }

            element.addEventListener(prefixes[i] + type, callback, false);
        }
    },


    setBackgroundImage: function (imagePath) {
        Ext.fly('admin-home-main-container').setStyle('background-image', 'url(' + imagePath + ')');
    },


    setVersionText: function (text) {
        Ext.fly('admin-home-version-info').setHTML(text);
    },


    setInstallationLabelText: function (text) {
        var labelText = text ? ' | ' + text : ''; // CMS-845
        Ext.fly('admin-home-installation-info').setHTML(labelText);
    },


    setLicensedToText: function (text) {
        Ext.fly('admin-home-login-licensed-to').setHTML(text);
    }

});
