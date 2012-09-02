Ext.define('Admin.store.account.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.CountryModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getAccountCountryUri(),
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty: 'total'
        }
    }
});