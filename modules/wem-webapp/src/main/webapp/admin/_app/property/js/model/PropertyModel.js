Ext.define('App.model.PropertyModel', {
    extend: 'Ext.data.Model',

    fields: [
        'name', 'value'
    ],

    idProperty: 'name'
});