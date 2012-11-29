Ext.define('Admin.store.contentStudio.ContentTypeTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentStudio.ContentTypeModel',

    folderSort: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/mock_contentStudioTree.json',
        reader: {
            type: 'json',
            totalProperty: 'total'
        }
    }

});