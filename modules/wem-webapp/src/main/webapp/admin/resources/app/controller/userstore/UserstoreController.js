Ext.define('Admin.controller.userstore.UserstoreController', {
    extend: 'Admin.controller.userstore.Controller',

    /*      Base controller for userstore model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },

    saveUserstoreToDB: function (userstore, callback) {
        var me = this;
        Ext.Ajax.request({
            url: 'data/userstore/config',
            method: 'POST',
            jsonData: userstore,
            success: function (response, opts) {
                var serverResponse = Ext.JSON.decode(response.responseText);
                if (!serverResponse.success) {
                    Ext.Msg.alert('Error', serverResponse.msg);
                } else {
                    callback(serverResponse.groupkey);
                }

            },
            failure: function (response, opts) {
                Ext.Msg.alert('Error', 'Unable to update userstore');
            }
        });
    },

    deleteUserstoreFromDB: function (group, callback) {
        //TODO
    }
});