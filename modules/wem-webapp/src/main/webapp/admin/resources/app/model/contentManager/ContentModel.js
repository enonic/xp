Ext.define('Admin.model.contentManager.ContentModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'type', 'owner', 'url',
        { name: 'lastModified', type: 'date', defaultValue: new Date() }
    ],

    idProperty: 'key'
});
