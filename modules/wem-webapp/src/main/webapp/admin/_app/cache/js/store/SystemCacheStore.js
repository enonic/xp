Ext.define( 'App.store.SystemCacheStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.SystemCacheModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: false,
    groupField: 'name',

    proxy: {
        type: 'ajax',
        url: 'data/system/cache/list',
        reader: {
            type: 'json',
            root: 'caches',
            totalProperty : 'total'
        }
    }
} );