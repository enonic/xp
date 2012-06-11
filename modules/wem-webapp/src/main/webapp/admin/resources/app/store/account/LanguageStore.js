Ext.define('Admin.store.account.LanguageStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.account.LanguageModel',

    pageSize: 100,
    autoLoad: true,

    sorters: [
        {
            property : 'languageCode',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app/account/js/data/Languages.json',
        reader: {
            type: 'json',
            root: 'languages',
            totalProperty : 'total'
        }
    }
});

