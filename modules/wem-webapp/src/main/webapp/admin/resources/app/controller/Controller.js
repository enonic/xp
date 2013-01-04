/**
 * Base controller for admin
 */
Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    requires: [
        'Admin.lib.UriHelper',
        'Admin.lib.RemoteService'
    ],

    init: function () {
    },


    /*  Getters */

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },

    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },

    getStartMenu: function () {
        return Ext.ComponentQuery.query('startMenu')[0];
    },

    getMainViewport: function () {
        var parent = window.parent || window;
        return parent.Ext.ComponentQuery.query('#mainViewport')[0];
    }

});