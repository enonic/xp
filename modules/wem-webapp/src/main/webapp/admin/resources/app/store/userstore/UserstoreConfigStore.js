Ext.define('Admin.store.userstore.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.userstore.UserstoreConfigModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'rest/userstore',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStores'
        },
        extraParams: {
            includeFields: false,
            includeConfig: true
        }
    }
});
