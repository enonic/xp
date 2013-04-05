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
                afterrender: me.initHomeScreen
            },

            'homescreen': {
                afterrender: function (view) {
                    if (me.isUserLoggedIn()) {

                        me.application.fireEvent('displayAppSelector');

                        /* For 18/4 demo */

                        // Can we move this higher up in order avoid seeing the background
                        if (me.getUrlHash() !== '') {
                            me.openPageInContentManager(me.getUrlHash());
                        }
                    } else {
                        me.application.fireEvent('displayLogin');
                    }

                    Admin.lib.RemoteService.system_getSystemInfo({}, function (r) {
                        view.setInstallationLabelText(r.installationName);
                        view.setVersionText(r.version);
                    });

                    view.setBackgroundImage('rest/ui/background.jpg');
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


    initHomeScreen: function () {
        var me = this;
        Ext.create('Admin.view.homescreen.Homescreen', {
            userIsLoggedIn: me.isUserLoggedIn()
        });
    },


    isUserLoggedIn: function () {
        var dummyCookie = Ext.util.Cookies.get('dummy_userIsLoggedIn');
        return dummyCookie && dummyCookie === 'true';
    },


    openApp: function (appModel, urlHash) {
        var me = this;
        me.getHomeScreenView().hideScreen();
        me.application.fireEvent('loadApplication', appModel.data, urlHash);
    },


    getAppsStore: function () {
        return this.getStore('Admin.store.homescreen.Apps');
    },


    /* For 18/4 demo */

    openPageInContentManager: function (urlHash) {
        var contentManagerAppData = {
            "id": "app-10",
            "name": "Content Manager",
            "description": "Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.",
            "appUrl": "app-content-manager.jsp",
            "icon": "resources/images/icons/metro/48x48/data.png"
        };
        var contentManagerAppModel = new Admin.model.homescreen.Apps(contentManagerAppData);

        this.openApp(contentManagerAppModel, urlHash);
    },


    getUrlHash: function () {
        return document.location.hash;
    },


    getHomeScreenView: function () {
        return Ext.ComponentQuery.query('homescreen')[0];
    },


    getLoginFormPanelView: function () {
        return Ext.ComponentQuery.query('loginPanel')[0];
    },


    getAppSelectorView: function () {
        return Ext.ComponentQuery.query('appSelector')[0];
    }

});