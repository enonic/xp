Ext.define('Admin.lib.RemoteService', {

    requires: ['Admin.lib.JsonRpcProvider'],

    singleton: true,

    handlerCache: {},

    init: function () {
        var config = {
            "url": API.util.getAbsoluteUri("admin/rest/jsonrpc"),
            "type": "jsonrpc",
            "namespace": "Admin.lib.RemoteService",
            "methods": [
                "account_find", "account_getGraph", "account_changePassword", "account_verifyUniqueEmail", "account_suggestUserName",
                "account_createOrUpdate", "account_delete", "account_get", "util_getCountries", "util_getLocales", "util_getTimeZones",
                "userstore_getAll", "userstore_get", "userstore_getConnectors", "userstore_createOrUpdate", "userstore_delete",
                "content_createOrUpdate", "content_list", "contentType_get", "content_tree", "content_get", "contentType_list",
                "content_delete", "content_validate", "content_find", "contentType_createOrUpdate", "contentType_delete",
                "contentType_tree",
                "schema_list", "schema_tree", "system_getSystemInfo", "mixin_get", "mixin_createOrUpdate", "mixin_delete",
                "relationshipType_get", "relationshipType_createOrUpdate", "relationshipType_delete", "space_list", "space_get",
                "space_delete", "space_createOrUpdate", "binary_create"
            ]
        };

        this.provider = Ext.Direct.addProvider(config);
        Ext.direct.RemotingProvider.enableBuffer = 20;
    },

    account_find: function (params, callback) {
        console.log(params, callback);
    },

    account_getGraph: function (params, callback) {
        console.log(params, callback);
    },

    account_changePassword: function (params, callback) {
        console.log(params, callback);
    },

    account_verifyUniqueEmail: function (params, callback) {
        console.log(params, callback);
    },

    account_suggestUserName: function (params, callback) {
        console.log(params, callback);
    },

    account_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    account_delete: function (params, callback) {
        console.log(params, callback);
    },

    account_get: function (params, callback) {
        console.log(params, callback);
    },

    util_getCountries: function (params, callback) {
        console.log(params, callback);
    },

    util_getLocales: function (params, callback) {
        console.log(params, callback);
    },

    util_getTimeZones: function (params, callback) {
        console.log(params, callback);
    },

    userstore_getAll: function (params, callback) {
        console.log(params, callback);
    },

    userstore_get: function (params, callback) {
        console.log(params, callback);
    },

    userstore_getConnectors: function (params, callback) {
        console.log(params, callback);
    },

    userstore_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    userstore_delete: function (params, callback) {
        console.log(params, callback);
    },

    content_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    contentType_get: function (params, callback) {
        console.log(params, callback);
    },

    content_list: function (params, callback) {
        console.log(params, callback);
    },

    content_tree: function (params, callback) {
        console.log(params, callback);
    },

    content_get: function (params, callback) {
        console.log(params, callback);
    },

    contentType_list: function (params, callback) {
        console.log(params, callback);
    },

    content_delete: function (params, callback) {
        console.log(params, callback);
    },

    content_find: function (params, callback) {
        console.log(params, callback);
    },

    content_validate: function (params, callback) {
        console.log(params, callback);
    },

    contentType_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    contentType_delete: function (params, callback) {
        console.log(params, callback);
    },

    contentType_tree: function (params, callback) {
        console.log(params, callback);
    },

    schema_tree: function (params, callback) {
        console.log(params, callback);
    },

    schema_list: function (params, callback) {
        console.log(params, callback);
    },

    system_getSystemInfo: function (params, callback) {
        console.log(params, callback);
    },

    mixin_get: function (params, callback) {
        console.log(params, callback);
    },

    mixin_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    mixin_delete: function (params, callback) {
        console.log(params, callback);
    },

    relationshipType_get: function (params, callback) {
        console.log(params, callback);
    },

    relationshipType_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    relationshipType_delete: function (params, callback) {
        console.log(params, callback);
    },

    space_list: function (params, callback) {
        console.log(params, callback);
    },

    space_get: function (params, callback) {
        console.log(params, callback);
    },

    space_delete: function (params, callback) {
        console.log(params, callback);
    },

    space_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },

    binary_create: function (params, callback) {
        console.log(params, callback);
    },

    getMethod: function (name) {
        var handler = this.handlerCache[name];

        if (handler) {
            return handler;
        }

        var method = new Ext.direct.RemotingMethod({name: name, len: 1});
        handler = this.provider.createHandler(null, method);
        this.handlerCache[name] = handler;
        return handler;
    },

    call: function (name, params, callback) {
        var method = this.getMethod(name);
        return method(params, callback);
    }

}, function () {
    this.init();
});

