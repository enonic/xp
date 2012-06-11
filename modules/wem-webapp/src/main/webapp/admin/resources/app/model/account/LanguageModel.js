Ext.define('Admin.model.account.LanguageModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'languageCode', 'description',
        {name: 'lastModified', type: 'date', dateFormat: 'Y-m-d H:i:s'}
    ],

    idProperty: 'key'
});
