Ext.define('Admin.controller.homescreen.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [
        'Admin.store.homescreen.Apps'
    ],

    models: [
        'Admin.model.homescreen.Apps'
    ],

    views: [
        'Admin.view.homescreen.Homescreen'
    ],


    init: function () {
        var me = this;

        me.control({
            '#mainViewport': {
                afterrender: me.initHomescreen
            },

            'homescreen': {
                afterrender: function (view) {
                    if (me.isUserLoggedIn()) {
                        me.application.fireEvent('displayAppSelector');
                    } else {
                        me.application.fireEvent('displayLogin');
                    }

                    Admin.lib.RemoteService.system_getSystemInfo({}, function (r) {
                        view.setInstallationLabelText(r.installationName);
                        view.setVersionText(r.version);
                    });

                    view.setBackgroundImage('http://www.wallsave.com/wallpapers/1280x800/squares/178115/squares-backgrounds-template-birthday-background-178115.jpg');
                    view.setLicensedToText('Licensed to Large Customer');
                },
                beforeshow: function () {
                    // Make sure window/frame has focus in order to get the keyboard navigation to work.
                    // Focus the filter text input as it is not possible to cross platform focus a window or element.
                    Ext.getCmp('admin-home-app-selector-search').focus(false, 10);
                }
            }
        });
    },


    initHomescreen: function () {
        var me = this;
        Ext.create('Admin.view.homescreen.Homescreen', {
            userIsLoggedIn: me.isUserLoggedIn()
        });
    },


    isUserLoggedIn: function () {
        var dummyCookie = Ext.util.Cookies.get('dummy_userIsLoggedIn');
        return dummyCookie && dummyCookie === 'true';
    },


    openApp: function (appModel) {
        var me = this;
        me.getHomeScreen().toggleShowHide();
        me.application.fireEvent('loadApplication', appModel.data);
    },


    getAppsStore: function () {
        return this.getStore('Admin.store.homescreen.Apps');
    },


    getHomeScreen: function () {
        return Ext.ComponentQuery.query('homescreen')[0];
    },


    getLoginFormPanel: function () {
        return Ext.ComponentQuery.query('loginPanel')[0];
    },


    getAppSelectorView: function () {
        return Ext.ComponentQuery.query('appSelector')[0];
    }

});