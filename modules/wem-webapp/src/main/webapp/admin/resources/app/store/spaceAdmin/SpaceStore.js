Ext.define('Admin.store.spaceAdmin.SpaceStore', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.spaceAdmin.SpaceModel',

    pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.space_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'spaces',
            totalProperty: 'total'
        }
    }
});