Ext.define( 'Admin.store.account.AccountStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.AccountModel',

    pageSize: 50,
    remoteSort: true,
    sorters: [{
        property: 'lastModified',
        direction: 'DESC'
    }],

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/account/search',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'results.accounts',
            totalProperty : 'results.total'
        }
    }
} );