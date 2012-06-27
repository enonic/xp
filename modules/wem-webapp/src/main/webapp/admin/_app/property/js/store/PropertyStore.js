Ext.define('App.store.PropertyStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.PropertyModel',

    pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: '_app/property/js/data/Properties.json',
        reader: {
            type: 'json',
            root: 'properties',
            totalProperty: 'total'
        }
    }
});