Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getAccountSearchUri: function () {
        return this.getAbsoluteUri('admin/rest/account');
    },

    getAccountIconUri: function (account) {
        return this.getAbsoluteUri('admin/rest/' + account.image_uri);
    },

    getUserUpdateUri: function (user) {
        return this.getAbsoluteUri("admin/rest/account/user/{0}/update", user.key);
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
