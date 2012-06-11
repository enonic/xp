Ext.define('App.model.SystemCacheModel', {
    extend: 'Ext.data.Model',

    fields: [
        'name',
        {name: 'memoryCapacity', type: 'int'},
        {name: 'diskCapacity', type: 'int'},
        {name: 'diskOverflow', type: 'boolean', defaultValue: false},
        {name: 'timeToLive', type: 'int'},
        {name: 'objectCount', type: 'int'},
        {name: 'cacheHits', type: 'int'},
        {name: 'cacheMisses', type: 'int'}
    ],

    idProperty: 'name'
});