Ext.define('Admin.controller.ContentController', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateContent: function (contentParams, callback) {
        api_remote.RemoteContentService.content_createOrUpdate(contentParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated, r.contentPath, r.contentId);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Internal error occured.");
            }
        });
    },

    remoteDeleteContent: function (contents, callback) {
        var me = this;
        var contentPaths = Ext.Array.map([].concat(contents), function (item) {
            return item.get('path');
        });
        api_remote.RemoteContentService.content_delete({"contentPaths": contentPaths }, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Internal error occured.");
            }
        });
    }

});