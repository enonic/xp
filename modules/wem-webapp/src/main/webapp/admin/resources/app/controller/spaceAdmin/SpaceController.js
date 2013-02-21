Ext.define('Admin.controller.spaceAdmin.SpaceController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

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