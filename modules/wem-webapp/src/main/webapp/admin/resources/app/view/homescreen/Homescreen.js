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


    updateGlobalView: function () {
        var me = this;
        me.setBackgroundImage('resources/images/x_710948main_typhoon_bopha_1600_1600-1200.jpg');
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
