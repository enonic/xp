Ext.define('Admin.store.account.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.account.UserstoreConfigModel',
    autoLoad: true,

    sorters: [
        {
            sorterFn: function (a, b) {
                var nameA = a.get('name').toLowerCase();
                var nameB = b.get('name').toLowerCase();
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                return 0;
            }
        }
    ],

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.userstore_getAll,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStores',
            totalProperty: 'total'
        }
    }
});
