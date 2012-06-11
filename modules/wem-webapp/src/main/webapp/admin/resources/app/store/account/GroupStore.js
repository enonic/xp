Ext.define('Admin.store.account.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/group/list',
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});