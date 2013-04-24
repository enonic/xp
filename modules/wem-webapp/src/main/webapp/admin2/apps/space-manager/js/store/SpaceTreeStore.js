Ext.define('Admin.store.SpaceTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.SpaceModel',

    proxy: {
        type: 'ajax',
        url: '../../../admin/resources/data/spacesTreeStub.json',
        reader: {
            type: 'json'
        }
    }

});
