Ext.define('Admin.store.userstore.UserstoreConnectorStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.userstore.UserstoreConnectorModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/userstore/connectors',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConnectors'
        }
    }
});
