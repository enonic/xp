Ext.define('Admin.model.account.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore', 'lastModified',
        'type', 'builtIn', 'editable'
    ],

    idProperty: 'key'
});
