Ext.define('Admin.model.account.CallingCodeModel', {
    extend: 'Ext.data.Model',

    idProperty: 'callingCode',

    fields: [
        'countryCode',
        'callingCode',
        'englishName',
        'localName'
    ],

    belongsTo: 'CountryModel'

});