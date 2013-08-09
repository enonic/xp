Ext.define('Admin.controller.SpaceController', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for Space model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateSpace: function (spaceParams, success, failure?) {
        api_remote.RemoteSpaceService.space_createOrUpdate(spaceParams, function (r) {
            success(r.created, r.updated);
        }, failure);
    },

    remoteDeleteSpace: function (spaces, success, failure?) {
        var me = this;
        var spaceNames = Ext.Array.map([].concat(spaces), function (item) {
            return item.get('name');
        });
        api_remote.RemoteSpaceService.space_delete({"spaceName": spaceNames}, success, failure);
    }

});