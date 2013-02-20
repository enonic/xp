Ext.define('Admin.model.contentStudio.SchemaModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'type', 'qualifiedName', 'displayName', 'module',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        'iconUrl'
    ],

    idProperty: 'key'
});