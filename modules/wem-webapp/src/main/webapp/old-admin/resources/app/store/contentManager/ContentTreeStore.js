Ext.define('Admin.store.contentManager.ContentTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentManager.ContentModel',

    folderSort: true,
    autoLoad: false,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.content_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contents',
            totalProperty: 'total'
        }
    }

});
