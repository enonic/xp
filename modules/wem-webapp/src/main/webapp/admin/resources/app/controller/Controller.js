/**
 * Base controller for admin
 */
Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*  Getters */

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },

    getLauncherToolbar: function () {
        var parent = window.parent.parent || window.parent;
        return parent ? parent.Ext.ComponentQuery.query('launcherToolbar')[0] : Ext.ComponentQuery.query('launcherToolbar')[0];
    }

});