Ext.define('Admin.controller.contentStudio.SchemaController', {
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

    remoteDeleteContentType: function (contentTypes, callback) {
        var me = this;
        var contentTypeNames = Ext.Array.map([].concat(contentTypes), function(item) {
            return item.get('qualifiedName');
        });
        Admin.lib.RemoteService.contentType_delete({"qualifiedContentTypeNames": contentTypeNames}, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete content type.");
            }
        });
    },

    remoteCreateOrUpdateMixin: function (params, callback) {
        Admin.lib.RemoteService.mixin_createOrUpdate(params, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to save mixin.");
            }
        });
    },

    remoteDeleteMixin: function (mixins, callback) {
        var me = this;
        var mixinNames = Ext.Array.map([].concat(mixins), function(item) {
            return item.get('qualifiedName');
        });
        Admin.lib.RemoteService.mixin_delete({"qualifiedMixinNames": mixinNames}, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete mixin.");
            }
        });
    },

    remoteCreateOrUpdateRelationshipType: function (params, callback) {
        Admin.lib.RemoteService.relationshipType_createOrUpdate(params, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to save relationship type.");
            }
        });
    },

    remoteDeleteRelationshipType: function (relationshipTypes, callback) {
        var me = this;
        var relationshipTypeNames = Ext.Array.map([].concat(relationshipTypes), function(item) {
            return item.get('qualifiedName');
        });
        Admin.lib.RemoteService.relationshipType_delete({"qualifiedRelationshipTypeNames": relationshipTypeNames}, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete relationship type.");
            }
        });
    }

});