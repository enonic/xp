Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    getContentManagerSearchUri: function () {
        return this.getAbsoluteUri('admin/common/data/contentManagerStub.json');
    },

    getAbsoluteUri: function (uri) {
        //TODO: Fix for typescript compiling
        //return window.CONFIG.baseUrl + '/' + uri;
    }

});
