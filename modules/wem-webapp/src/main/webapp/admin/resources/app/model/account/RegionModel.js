Ext.define('Admin.model.account.RegionModel', {
    extend: 'Ext.data.Model',

    idField: 'regionCode',

    fields: [
        'countryCode',
        'regionCode',
        'englishName',
        'localName'
    ],

    belongsTo: 'CountryModel'

});
