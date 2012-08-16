Ext.define('Admin.store.account.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getAccountSearchUri(),
        extraParams: {
            types: 'group,role'
        },
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});