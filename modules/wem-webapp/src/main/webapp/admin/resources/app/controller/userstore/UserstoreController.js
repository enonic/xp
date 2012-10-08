Ext.define('Admin.controller.userstore.UserstoreController', {
    extend: 'Admin.controller.userstore.Controller',

    /*      Base controller for userstore model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },

    saveUserstoreToDB: function (userstore, callback) {

        Admin.lib.RemoteService.userstore_createOrUpdate(userstore,
            function (response) {
                if (response && response.success) {
                    if (Ext.isFunction(callback)) {
                        callback(userstore.name);
                    }
                } else {
                    Ext.Msg.alert('Error',
                        (response && response.error && response.error.message) ? response.error.message : "Unknown error.");
                }
            });
    },

    deleteUserstoreFromDB: function (name, callback) {

        Admin.lib.RemoteService.userstore_delete({name: name},
            function (response) {
                if (response && response.success) {
                    if (Ext.isFunction(callback)) {
                        callback(response.deleted > 0);
                    }
                } else {
                    Ext.Msg.alert('Error',
                        (response && response.error && response.error.message) ? response.error.message : "Unknown error.");
                }
            });
    }
});