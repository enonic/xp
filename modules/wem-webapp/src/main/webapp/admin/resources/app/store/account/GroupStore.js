Ext.define('Admin.store.account.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.account_find,
        extraParams: {
            types: ['group','role']
        },
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});
