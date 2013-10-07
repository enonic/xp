Ext.define('Admin.model.account.CountryModel', {
    extend: 'Ext.data.Model',

    requires: [
        'Admin.model.account.RegionModel',
        'Admin.model.account.CallingCodeModel'
    ],

    idProperty: 'code',

    fields: [
        'code',
        'englishName',
        'localName',
        'regionsEnglishName',
        'regionsLocalName'
    ],

    hasMany: [
        { model: 'Admin.model.account.RegionModel', name: 'regions' },
        { model: 'Admin.model.account.CallingCodeModel', name: 'callingCodes' }
    ]

});
