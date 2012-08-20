Ext.define('Admin.store.contentManager.ContentTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentManager.ContentModel',

    folderSort: true,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getContentManagerSearchTreeUri(),
        reader: {
            type: 'json',
            totalProperty: 'total'
        }
    }

});