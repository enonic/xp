Ext.define('Admin.controller.contentStudio.ContentTypeController', {
    extend: 'Admin.controller.contentStudio.Controller',

    /*      Base controller for content type model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    remoteCreateOrUpdateContentType: function (contentTypeParams, callback) {
        Admin.lib.RemoteService.contentType_createOrUpdate(contentTypeParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to save content type.");
            }
        });
    },

    remoteDeleteContentType: function (contentType, callback) {
        var me = this;
        Admin.lib.RemoteService.contentType_delete({"qualifiedContentTypeNames": [contentType.qualifiedName]}, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete content type.");
            }
        });
    }

});