Ext.define('Admin.model.spaceAdmin.SpaceModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'displayName', 'description', 'image_url',
        {name: 'createdTime', type: 'date', default: new Date()},
        {name: 'modifiedTime', type: 'date', default: new Date()}
    ],

    idProperty: 'key'
});