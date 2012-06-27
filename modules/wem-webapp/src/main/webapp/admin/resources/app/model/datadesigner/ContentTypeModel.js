Ext.define('Admin.model.datadesigner.ContentTypeModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'type', 'extends', 'created', 'lastModified', 'name', 'displayName', 'module', 'configXml', 'usageCount', 'icon'
    ],

    idProperty: 'key'
});
