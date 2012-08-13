Ext.define('Admin.store.account.TimezoneStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.TimezoneModel',

    pageSize: 50,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getAccountTimezoneUri(),
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'timezones',
            totalProperty: 'total'
        }
    }
});