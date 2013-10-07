Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getContentManagerSearchUri: function () {
        return this.getAbsoluteUri('admin/resources/data/contentManagerStub.json');
    },

    getAbsoluteUri: function (uri) {
        //TODO: Fix for typescript compiling
        //return window.CONFIG.baseUrl + '/' + uri;
    }

});
