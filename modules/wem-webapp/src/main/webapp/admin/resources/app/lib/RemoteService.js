Ext.define('Admin.lib.RemoteService', {

    requires: ['Admin.lib.UriHelper', 'Admin.lib.JsonRpcProvider'],

    singleton: true,

    init: function () {
        var config = {
            "url": Admin.lib.UriHelper.getAbsoluteUri("admin/rest/jsonrpc"),
            "type": "jsonrpc",
            "namespace": "Admin.lib.RemoteService",
            "methods": [
                "account_find", "account_getGraph", "account_changePassword", "util_getCountries", "util_getLocales", "util_getTimeZones"
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

