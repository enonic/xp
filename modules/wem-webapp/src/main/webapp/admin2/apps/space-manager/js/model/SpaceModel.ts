Ext.define('Admin.model.SpaceModel', {
    extend: 'Ext.data.Model',

    fields: <any[]>[
        'name', 'displayName', 'iconUrl', 'rootContentId',
        {name: 'createdTime', type: 'date', default: new Date()},
        {name: 'modifiedTime', type: 'date', default: new Date()},
        { name: 'editable', type: 'boolean' },
        { name: 'deletable', type: 'boolean' }
    ],

    idProperty: 'name'
});
