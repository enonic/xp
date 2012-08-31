Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getAccountSearchUri: function () {
        return this.getAbsoluteUri('admin/rest/account');
    },

    getAccountCountryUri: function () {
        return this.getAbsoluteUri('admin/rest/misc/country');
    },

    getAccountTimezoneUri: function () {
        return this.getAbsoluteUri('admin/rest/misc/timezone');
    },

    getAccountLocaleUri: function () {
        return this.getAbsoluteUri('admin/rest/misc/locale');
    },

    getAccountDeleteUri: function () {
        return this.getAbsoluteUri('admin/rest/account/delete');
    },

    getAccountSuggestUserNameUri: function () {
        this.getAbsoluteUri('admin/rest/account/user/suggest-name');
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
