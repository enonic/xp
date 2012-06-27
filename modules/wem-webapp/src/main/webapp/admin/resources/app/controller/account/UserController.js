Ext.define('Admin.controller.account.UserController', {
    extend: 'Admin.controller.account.Controller',

    /*      Base controller for user model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    saveUserToDB: function (user, callback) {
        var me = this;
        Ext.Ajax.request({
            url: 'data/user/update',
            method: 'POST',
            jsonData: user,
            success: function (response, opts) {
                var serverResponse = Ext.JSON.decode(response.responseText);
                if (!serverResponse.success) {
                    Ext.Msg.alert('Error', serverResponse.error);
                } else {
                    callback(serverResponse.userkey);
                }
                var current = me.getAccountGridPanel().store.currentPage;
                me.getAccountGridPanel().store.loadPage(current);
            },
            failure: function (response, opts) {
                Ext.Msg.alert('Error', 'Unable to update user');
            }
        });

    },

    changePasswordInDB: function (user, callback) {
        //TODO
    },

    deleteUserFromDB: function (user, callback) {
        //TODO
    }

});