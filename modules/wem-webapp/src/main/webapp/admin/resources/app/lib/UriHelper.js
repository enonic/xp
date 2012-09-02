Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getAccountSearchUri: function () {
        return this.getAbsoluteUri('admin/rest/account');
    },

    getAccountDeleteUri: function () {
        return this.getAbsoluteUri('admin/rest/account/delete');
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

    getAccountVerifyEmailUri: function () {
        return this.getAbsoluteUri('admin/rest/account/user/verify-unique-email');
    },

    getAccountChangePasswordUri: function (account) {
        return Ext.String.format("admin/rest/account/user/{0}/change-password", account.key);
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
