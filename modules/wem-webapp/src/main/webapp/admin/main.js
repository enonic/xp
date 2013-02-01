Ext.require('Ext.app.Application');
var mainApp;

Ext.onReady(function () {
    mainApp = Ext.create('Ext.app.Application', {
        name: 'App',
        appFolder: '_app/main/js',

        requires: [
            'Admin.lib.Ping',
            'Admin.lib.UriHelper'
        ],

        controllers: [
            'Admin.controller.homescreen.Controller',
            'Admin.controller.homescreen.Login',
            'Admin.controller.homescreen.AppSelector',
            'Admin.controller.TopBarController',
            'Admin.controller.NotifyUserController'
        ],

        launch: function () {
            Ext.create('Ext.container.Viewport', {
                id: 'mainViewport',
                layout: 'border',
                style: 'border: medium none',
                padding: 0,
                items: [
                    {
                        region: 'center',
                        bodyCls: 'main-viewport-center-body',
                        html: '<div id="appFrames" style="height: 100%; width: 100%;"></div>'
                    }
                ]
            });

            Admin.lib.Ping.startPolling();
        }
    });
});