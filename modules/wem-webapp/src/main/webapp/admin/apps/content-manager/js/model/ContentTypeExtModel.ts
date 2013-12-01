Ext.define('Admin.model.schemaManager.ContentTypeModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'name',
        'displayName',
        'module',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        'configXML',
        'iconUrl'
    ],

    idProperty: 'name'
});
