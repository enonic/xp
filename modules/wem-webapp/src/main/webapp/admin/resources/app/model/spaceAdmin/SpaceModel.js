Ext.define('Admin.model.spaceAdmin.SpaceModel', {
    extend: 'Ext.data.Model',

    fields: [
        'name', 'displayName', 'iconUrl', 'rootContentId',
        {name: 'createdTime', type: 'date', default: new Date()},
        {name: 'modifiedTime', type: 'date', default: new Date()}
    ],

    idProperty: 'name'
});