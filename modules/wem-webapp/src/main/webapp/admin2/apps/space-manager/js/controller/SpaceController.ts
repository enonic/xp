Ext.define('Admin.controller.SpaceController', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for Space model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateSpace: function (spaceParams, callback) {
        api_remote.RemoteService.space_createOrUpdate(spaceParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "An unexpected error occurred.");
            }
        });
    },

    remoteDeleteSpace: function (spaces, callback) {
        var me = this;
        var spaceNames = Ext.Array.map([].concat(spaces), function (item) {
            return item.get('name');
        });
        api_remote.RemoteService.space_delete({"spaceName": spaceNames}, function (r) {
            if (r) {
                callback.call(me, r.success, r);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete space.");
            }
        });
    }

});