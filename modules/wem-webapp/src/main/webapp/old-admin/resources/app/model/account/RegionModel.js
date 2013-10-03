Ext.define('Admin.model.account.RegionModel', {
    extend: 'Ext.data.Model',

    idProperty: 'regionCode',

    fields: [
        'countryCode',
        'regionCode',
        'englishName',
        'localName'
    ],

    belongsTo: 'CountryModel'

});
