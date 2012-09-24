Ext.define('Admin.store.userstore.UserstoreConnectorStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.userstore.UserstoreConnectorModel',

    autoLoad: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.userstore_getConnectors,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConnectors',
            totalProperty: 'total'
        }
    }
});
