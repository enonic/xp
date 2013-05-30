Ext.define('Admin.store.contentManager.ContentStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.contentManager.ContentModel',

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.content_find,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contents',
            totalProperty: 'total'
        }
    }
});