Ext.define('Admin.store.account.AccountStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.AccountModel',

    // set to 200 to workaround infinite grid issue
    // http://www.sencha.com/forum/showthread.php?237251-Infinite-grid-does-not-work-with-small-page-size
    // can be safely reduced back to 50 after this is fixed
    pageSize: 200,
    buffered: true,
    remoteSort: true,
    sorters: [
        {
            property: 'lastModified',
            direction: 'DESC'
        }
    ],

    autoLoad: false,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.account_find,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'accounts',
            totalProperty: 'total'
        }
    }
});