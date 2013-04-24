Ext.define('Admin.store.spaceAdmin.SpaceTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.spaceAdmin.SpaceModel',

    proxy: {
        type: 'ajax',
        url: '../../../admin/resources/data/spacesTreeStub.json',
        reader: {
            type: 'json'
        }
    }

});
