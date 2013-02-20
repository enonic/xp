Ext.define('Admin.store.contentStudio.SchemaStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.contentStudio.SchemaModel',

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