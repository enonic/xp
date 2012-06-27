Ext.define('Admin.store.datadesigner.ContentTypeStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.datadesigner.ContentTypeModel',

    remoteSort: true,
    sorters: [
        {
            property: 'lastModified',
            direction: 'DESC'
        }
    ],
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/mock_datadesignerGrid.json',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'results.contentTypes',
            totalProperty: 'results.total'
        }
    }
});