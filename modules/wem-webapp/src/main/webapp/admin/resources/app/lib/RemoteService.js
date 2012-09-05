Ext.define('Admin.lib.RemoteService', {

    requires: ['Admin.lib.UriHelper', 'Admin.lib.JsonRpcProvider'],

    singleton: true,

    init: function () {
        var config = {
            "url": Admin.lib.UriHelper.getAbsoluteUri("admin/rest/jsonrpc"),
            "type": "jsonrpc",
            "namespace": "Admin.lib.RemoteService",
            "methods": [
                "account_find", "account_getGraph", "account_changePassword", "account_verifyUniqueEmail", "account_suggestUserName",
                "account_createOrUpdate", "account_delete", "account_get", "util_getCountries", "util_getLocales", "util_getTimeZones"
            ]
        };

        Ext.Direct.addProvider(config);
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
    }

}, function () {
    this.init();
});

