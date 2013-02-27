Ext.define('Admin.controller.spaceAdmin.SpaceController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Base controller for Space model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateSpace: function (spaceParams, callback) {
        Admin.lib.RemoteService.space_createOrUpdate(spaceParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "An unexpected error occurred.");
            }
        });
    },

    remoteDeleteSpace: function (spaceNames, callback) {
        var me = this;
        Admin.lib.RemoteService.space_delete({"spaceName": spaceNames}, function (r) {
            if (r) {
                callback.call(me, r.success, r);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete space.");
            }
        });
    }

});