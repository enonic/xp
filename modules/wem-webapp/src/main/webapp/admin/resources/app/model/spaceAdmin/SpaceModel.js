Ext.define('Admin.model.spaceAdmin.SpaceModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'displayName', 'description', 'iconUrl',
        {name: 'createdTime', type: 'date', default: new Date()},
        {name: 'modifiedTime', type: 'date', default: new Date()}
    ],

    idProperty: 'key'
});