Ext.define('Admin.model.account.CountryModel', {
    extend: 'Ext.data.Model',

    idField: 'code',

    fields: [
        'code',
        'englishName',
        'localName',
        'regionsEnglishName',
        'regionsLocalName'
    ],

    proxy: {
        type: 'ajax',
        url: 'data/misc/country/list',
        reader: {
            idProperty: 'code',
            type: 'json',
            root: 'countries',
            totalProperty: 'total'
        }
    },

    hasMany: [
        { model: 'Admin.model.account.RegionModel', name: 'regions' },
        { model: 'Admin.model.account.CallingCodeModel', name: 'callingCodes' }
    ]

});
