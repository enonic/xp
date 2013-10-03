Ext.define('Admin.store.schemaManager.SchemaStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.schemaManager.SchemaModel',

    pageSize: 50,
    remoteSort: true,
    sorters: [
        {
            property: 'modifiedTime',
            direction: 'DESC'
        }
    ],
    autoLoad: false,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.schema_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'schemas',
            totalProperty: 'total'
        }
    }
});