Ext.define('Admin.store.account.AccountStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.AccountModel',

    pageSize: 50,
    remoteSort: true,
    sorters: [
        {
            property: 'lastModified',
            direction: 'DESC'
        }
    ],

    autoLoad: false,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getAccountSearchUri(),
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'accounts',
            totalProperty: 'total'
        }
    }
});