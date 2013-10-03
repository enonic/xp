Ext.define('Admin.model.schemaManager.SchemaModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'key', 'name', 'type', 'qualifiedName', 'displayName', 'module',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        'iconUrl'
    ],

    idProperty: 'key'
});