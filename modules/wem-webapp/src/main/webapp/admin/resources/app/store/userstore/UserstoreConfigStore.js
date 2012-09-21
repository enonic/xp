Ext.define('Admin.store.userstore.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.userstore.UserstoreConfigModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.userstore_getAll,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStores',
            totalProperty: 'total'
        }
    }
});
