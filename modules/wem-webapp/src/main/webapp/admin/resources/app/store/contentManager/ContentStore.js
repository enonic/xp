Ext.define('Admin.store.contentManager.ContentStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.contentManager.ContentModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: Admin.lib.UriHelper.getContentManagerSearchUri(),
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'results.content',
            totalProperty: 'results.total'
        }
    }
});