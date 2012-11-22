Ext.define('Admin.controller.contentManager.ContentController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateContent: function (contentParams, callback) {
        Admin.lib.RemoteService.content_createOrUpdate(contentParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            }
        });
    },

    remoteDeleteContent: function (content, callback) {
        //TODO
        var me = this;
        Admin.lib.RemoteService.content_delete({"contentPaths": [content.path]}, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            }
        });
    }

});