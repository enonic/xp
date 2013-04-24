Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getContentManagerSearchUri: function () {
        return this.getAbsoluteUri('admin/resources/data/contentManagerStub.json');
    },

    getAbsoluteUri: function (uri) {
        return window.CONFIG.baseUrl + '/' + uri;
    }

});
