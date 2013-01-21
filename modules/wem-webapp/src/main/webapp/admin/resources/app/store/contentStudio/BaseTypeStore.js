Ext.define('Admin.store.contentStudio.BaseTypeStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.contentStudio.BaseTypeModel',

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
        directFn: Admin.lib.RemoteService.baseType_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'baseTypes',
            totalProperty: 'total'
        }
    }
});