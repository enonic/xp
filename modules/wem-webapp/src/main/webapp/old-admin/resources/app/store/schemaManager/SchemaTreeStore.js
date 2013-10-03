Ext.define('Admin.store.schemaManager.SchemaTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.schemaManager.SchemaModel',

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