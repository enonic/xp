Ext.define('Admin.store.spaceAdmin.SpaceStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.spaceAdmin.SpaceModel',

    pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/spacesStub.json',
        reader: {
            type: 'json',
            root: 'spaces'
        }
    }
});