Ext.define('Admin.store.account.TimezoneStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.TimezoneModel',

    pageSize: 50,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/timezone/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'timezones',
            totalProperty : 'total'
        }
    }
});