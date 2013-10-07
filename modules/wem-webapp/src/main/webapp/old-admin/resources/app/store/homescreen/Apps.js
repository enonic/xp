Ext.define('Admin.store.homescreen.Apps', {
    extend: 'Ext.data.Store',

    model: 'Admin.model.homescreen.Apps',

    pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/applications.json',
        reader: {
            type: 'json',
            root: 'applications'
        }
    }
});