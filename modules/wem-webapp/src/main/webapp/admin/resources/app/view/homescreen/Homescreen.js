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
        html: Templates.homescreen.mainContainerHtml
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
