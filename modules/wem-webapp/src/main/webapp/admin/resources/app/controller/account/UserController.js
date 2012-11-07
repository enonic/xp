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
        user.key = user.key || ['user', user.userStore, user.name].join(':');
        Admin.lib.RemoteService.account_createOrUpdate(user, function (r) {
            if (r && r.success) {
                callback(user.key);
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