Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getAccountSearchUri: function () {
        return this.getAbsoluteUri('admin/rest/account');
    },

    getAccountSuggestUserNameUri: function () {
        return this.getAbsoluteUri('admin/rest/account/user/suggest-name');
    },

    getAccountInfoUri: function (account) {
        return this.getAbsoluteUri('admin/rest/' + account.info_uri);
    },

    getAccountGraphUri: function (account) {
        return this.getAbsoluteUri('admin/rest/' + account.graph_uri);
    },

    getAccountIconUri: function (account) {
        return this.getAbsoluteUri('admin/rest/' + account.image_uri);
    },

    getAccountChangePasswordUri: function (account) {
        return Ext.String.format("admin/rest/account/user/{0}/change-password", account.key);
    },

    getUserUpdateUri: function (user) {
        return this.getAbsoluteUri("admin/rest/account/user/{0}/update", user.key);
    },

    getUserstoreSearchUri: function () {
        return this.getAbsoluteUri('admin/rest/userstore/search');
    },

    getContentManagerSearchUri: function () {
        return this.getAbsoluteUri('admin/resources/data/contentManagerStub.json');
    },

    getContentManagerSearchTreeUri: function () {
        return this.getAbsoluteUri('admin/resources/data/contentManagerTreeStub.json');
    },

    getAbsoluteUri: function (uri) {
        return window.CONFIG.baseUrl + '/' + uri;
    }

});
