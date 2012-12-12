Ext.define('Admin.store.contentStudio.ContentTypeTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentStudio.ContentTypeModel',

    folderSort: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.contentType_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty: 'total'
        }
    }

});