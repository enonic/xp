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
        'Admin.lib.RemoteService',
        'Admin.view.FeedbackBox'
    ],

    init: function () {
        Ext.create('widget.feedbackBox');
    },


    /*  Getters */

    getFeedbackBox: function () {
        return Ext.ComponentQuery.query('feedbackBox')[0];
    },

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },

    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },

    getStartMenu: function () {
        return Ext.ComponentQuery.query('startMenu')[0];
    }

});