Ext.define('Admin.model.account.CallingCodeModel', {
    extend: 'Ext.data.Model',

    idProperty: 'callingCodeId',

    fields: [
        'callingCodeId',
        'callingCode',
        'englishName',
        'localName'
    ],

    belongsTo: 'CountryModel'

});