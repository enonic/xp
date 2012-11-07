Ext.define('Admin.controller.contentManager.ContentController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    saveContentToDB: function (contentParams, callback) {
        Admin.lib.RemoteService.content_createOrUpdate(contentParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            }
        });
    },

    deleteContentFromDB: function (content, callback) {
        //TODO
        var success = true;
        if (Ext.isFunction(callback)) {
            callback.call(this, success);
        }
    }

});