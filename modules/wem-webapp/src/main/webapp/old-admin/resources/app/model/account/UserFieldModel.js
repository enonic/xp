Ext.define('Admin.model.account.UserFieldModel', {
    extend: 'Ext.data.Model',
    fields: [
        'type',
        'readOnly',
        'required',
        'remote',
        'iso'
    ],

    idProperty: 'id'
});
