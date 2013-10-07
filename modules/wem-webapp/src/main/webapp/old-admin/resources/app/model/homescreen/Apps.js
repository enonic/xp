Ext.define('Admin.model.homescreen.Apps', {
    extend: 'Ext.data.Model',

    fields: [
        'id', 'name', 'description', 'appUrl', 'icon'
    ],

    idProperty: 'id'
});