Ext.define('Admin.store.userstore.UserStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.AccountModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'data/user/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'users',
            totalProperty: 'total'
        }
    }
});
