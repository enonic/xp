Ext.define('Admin.controller.account.GroupController', {
    extend: 'Admin.controller.account.Controller',

    /*      Base controller for group model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    saveGroupToDB: function (group, callback) {
        var me = this;
        Admin.lib.RemoteService.account_createOrUpdate(group, function (r) {
            if (r && r.success) {
                callback(group.key);
            }
            var current = me.getAccountGridPanel().store.currentPage;
            me.getAccountGridPanel().store.loadPage(current);
        });
    },

    deleteGroupFromDB: function (group, callback) {
        //TODO
    }

});