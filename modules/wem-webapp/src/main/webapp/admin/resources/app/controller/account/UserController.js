Ext.define('Admin.controller.account.UserController', {
    extend: 'Admin.controller.account.Controller',

    /*      Base controller for user model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateUser: function (user, callback) {
        var me = this;
        user.key = user.key || ['user', user.userStore, user.name].join(':');
        Admin.lib.RemoteService.account_createOrUpdate(user, function (r) {
            if (r && r.success) {
                callback(user.key);
            }
        });
    },

    remoteChangePassword: function (user, callback) {
        //TODO: should accept array/single user model(s)
    },

    remoteDeleteUser: function (user, callback) {
        //TODO: should accept array/single user model(s)
    }

});