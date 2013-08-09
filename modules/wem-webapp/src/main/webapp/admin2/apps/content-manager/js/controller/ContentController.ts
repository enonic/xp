Ext.define('Admin.controller.ContentController', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateContent: function (contentParams, success, failure?) {
        api_remote.RemoteContentService.content_createOrUpdate(contentParams, function (result ) {
            success(result.created, result.updated, result.contentPath, result.contentId);
        }, failure);
    },

    remoteDeleteContent: function (contents, success, failure?) {
        var me = this;
        var contentPaths = Ext.Array.map([].concat(contents), function (item) {
            return item.get('path');
        });
        api_remote.RemoteContentService.content_delete({"contentPaths": contentPaths }, success, failure);
    }

});