Ext.define('Admin.model.templateManager.TemplateModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'key', 'displayName', 'name', 'description', 'url', 'siteContent', 'version', 'modules', 'contentFilter', 'deletable', 'editable'
    ],

    idProperty: 'key'
});