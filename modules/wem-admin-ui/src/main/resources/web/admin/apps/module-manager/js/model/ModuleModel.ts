Ext.define('Admin.model.moduleManager.ModuleModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'key', 'version', 'displayName', 'name', 'url', 'info', 'vendorName', 'vendorUrl', 'deletable', 'editable'
    ],

    idProperty: 'key'
});