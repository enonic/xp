Ext.define('Admin.store.account.CallingCodeStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.CallingCodeModel',

    autoLoad: false,

    remoteFilter: false,

    proxy: {
        type: 'ajax',
        url: 'data/misc/callingcodes/list',
        reader: {
            type: 'json',
            root: 'codes'
        }
    }
});