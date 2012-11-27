Ext.define('Admin.store.datadesigner.ContentTypeStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.datadesigner.ContentTypeModel',

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
        directFn: Admin.lib.RemoteService.contentType_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty: 'total'
        }
    }
});