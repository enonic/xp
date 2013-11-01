Ext.define('Admin.model.schemaManager.SchemaModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'key', 'name', 'type', 'qualifiedName', 'displayName', 'module', 'iconUrl',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        { name: 'editable', type: 'boolean' },
        { name: 'deletable', type: 'boolean' },
        { name: 'hasChildren', type: 'boolean', defaultValue: false },
        {
            name: 'leaf', // property needed for TreeStore
            type: 'boolean',
            convert: function (value, record) {
                return !record.get('hasChildren');
            }
        }
    ],

    idProperty: 'name'
});