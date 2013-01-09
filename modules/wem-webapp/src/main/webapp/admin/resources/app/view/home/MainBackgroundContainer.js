Ext.define('Admin.view.home.MainBackgroundContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.homeMainBackgroundContainer',
    requires: [
        'Admin.view.home.LoginPanel',
        'Admin.view.home.AppSelector'
    ],

    autoEl: {
        tag: 'div',
        cls: 'clickable',
        id: 'admin-home-main-container',
        html: Templates.home.mainContainerHtml
    },
    floating: true,
    hideMode: 'offsets',
    renderTo: Ext.getBody(),

    loginPanel: null,
    appSelector: null,

    initComponent: function () {
        var me = this;

        me.on('render', function () {
            me.updateView();
            me.addClickEvent();

            Ext.create('Admin.view.home.LoginPanel');
            Ext.create('Admin.view.home.AppSelector');
        });

        me.callParent(arguments);
    },


    updateView: function () {
        var me = this;

        // For demo purposes. Should be removed!
        Ext.getBody().on('click', function (evt, el) {
            if (el.tagName === 'BODY') {
                me.toggleShowHide();
            }
        });


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
