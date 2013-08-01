Ext.define('Admin.store.schemaManager.ContentTypeStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.schemaManager.ContentTypeModel',

    pageSize: 50,
    remoteSort: true,
    sorters: [
        {
            property: 'modifiedTime',
            direction: 'DESC'
        }
    ],
    autoLoad: true,

    proxy: {
        type: 'direct',
        directFn: api_remote.RemoteContentTypeService.contentType_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty: 'total'
        }
    }
});