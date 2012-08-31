Ext.define('Admin.store.account.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        // type: 'ajax',
        // url: Admin.lib.UriHelper.getAccountSearchUri(),
        type: 'direct',
        directFn: Admin.lib.RemoteService.account_search,
        extraParams: {
            types: 'group,role'
        },
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});