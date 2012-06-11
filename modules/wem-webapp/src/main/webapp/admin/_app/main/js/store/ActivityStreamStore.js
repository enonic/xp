Ext.define('App.store.ActivityStreamStore', {
    extend: 'Ext.data.Store',
    model: 'App.model.ActivityStreamModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: '_app/main/data/ActivityStream.json',
        reader: {
            type: 'json',
            root: 'activitystreams'
        }
    }
});
