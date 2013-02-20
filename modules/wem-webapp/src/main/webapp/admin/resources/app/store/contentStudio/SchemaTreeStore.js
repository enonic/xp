Ext.define('Admin.store.contentStudio.SchemaTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentStudio.SchemaModel',

    folderSort: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.schema_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'schemas',
            totalProperty: 'total'
        }
    }

});