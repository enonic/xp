Ext.define('Admin.store.contentManager.ContentStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.contentManager.ContentModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/contentManagerStub.json',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'results.content',
            totalProperty: 'results.total'
        }
    }
});