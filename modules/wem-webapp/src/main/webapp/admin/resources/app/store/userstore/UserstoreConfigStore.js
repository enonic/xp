Ext.define('Admin.store.userstore.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.userstore.UserstoreConfigModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'data/userstore/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConfigs'
        },
        extraParams: {
            includeFields: false,
            includeConfig: true
        }
    }
});
