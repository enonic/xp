Ext.define('Admin.store.datadesigner.ContentTypeTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.datadesigner.ContentTypeModel',

    folderSort: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/mock_datadesignerTree.json',
        reader: {
            type: 'json',
            totalProperty: 'total'
        }
    }

});