Ext.define('Admin.controller.account.GroupController', {
    extend: 'Admin.controller.account.Controller',

    /*      Base controller for group model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateGroup: function (group, callback) {
        var me = this;
        group.key = group.key || ['group', group.userStore, group.name].join(':');
        Admin.lib.RemoteService.account_createOrUpdate(group, function (r) {
            if (r && r.success) {
                callback(group.key);
            }
        });
    },

    remoteDeleteGroup: function (group, callback) {
        //TODO
    }

});