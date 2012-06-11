Ext.define( 'Admin.model.account.CallingCodeModel', {
    extend: 'Ext.data.Model',

    idField: 'callingCode',

    fields: [
        'countryCode',
        'callingCode',
        'englishName',
        'localName'
    ],

    belongsTo: 'CountryModel'

} );