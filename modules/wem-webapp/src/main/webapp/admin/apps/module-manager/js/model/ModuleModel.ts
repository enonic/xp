Ext.define('Admin.model.moduleManager.ModuleModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'moduleKey', 'version', 'displayName', 'url', 'info', 'vendorName', 'vendorUrl',
        'maxSystemVersion', 'minSystemVersion'
    ],

    idProperty: 'moduleKey'
});