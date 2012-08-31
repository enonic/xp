Ext.define('Admin.lib.RemoteService', {

    requires: ['Admin.lib.UriHelper'],

    singleton: true,

    methodsToRegister: [
        "account_search", "account_getGraph", "account_changePassword", "util_getCountries", "util_getLocales", "util_getTimeZones"
    ],

    init: function () {
        var config = {
            "url": Admin.lib.UriHelper.getExtDirectUri(),
            "type": "remoting",
            "namespace": "Admin.lib",
            "actions": {
                "RemoteService": []
            }
        };

        for ( var i = 0; i < this.methodsToRegister.length; i++ ) {
            config.actions.RemoteService.push({"name": this.methodsToRegister[i], "len": 1})
        }

        Ext.Direct.addProvider(config);
        Ext.direct.RemotingProvider.enableBuffer = 20;
    },

    account_search: function (params, callback) {
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

});

Admin.lib.RemoteService.init();

