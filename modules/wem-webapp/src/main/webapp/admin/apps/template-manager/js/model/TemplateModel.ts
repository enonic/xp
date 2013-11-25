Ext.define('Admin.model.templateManager.TemplateModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'id', 'displayName', 'path', 'iconUrl', 'hasChildren'
    ],

    idProperty: 'id'
});