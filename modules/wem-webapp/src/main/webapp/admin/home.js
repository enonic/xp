Ext.require('Ext.app.Application');
var mainApp;

Ext.onReady(function () {
    mainApp = Ext.create('Ext.app.Application', {
        name: 'App',
        appFolder: '_app/main/js',

        controllers: [
            'Admin.controller.home.Controller'
        ],

        launch: function () {
            Ext.create('Ext.container.Viewport', {
                id: 'mainViewport',
                layout: 'border',
                style: 'border: medium none',
                padding: 0
            });
        }
    });
});