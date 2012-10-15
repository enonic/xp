Ext.define('Admin.model.datadesigner.ContentTypeModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'type', 'extends', 'name', 'displayName', 'module', 'configXml', 'usageCount', 'icon',
        { name: 'created', type: 'date', defaultValue: new Date() },
        { name: 'lastModified', type: 'date', defaultValue: new Date() }
    ],

    idProperty: 'key'
});
