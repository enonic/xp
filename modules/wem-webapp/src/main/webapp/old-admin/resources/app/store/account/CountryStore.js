Ext.define('Admin.store.account.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.CountryModel',

    autoLoad: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.util_getCountries,
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty: 'total'
        }
    }
});