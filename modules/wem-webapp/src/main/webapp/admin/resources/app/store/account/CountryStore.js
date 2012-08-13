Ext.define('Admin.store.account.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.CountryModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: Admin.plugin.UriHelper.getAccountCountryUri(),
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty: 'total'
        }
    }
});