Ext.define('Admin.store.schemaManager.ContentTypeTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.schemaManager.ContentTypeModel',

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