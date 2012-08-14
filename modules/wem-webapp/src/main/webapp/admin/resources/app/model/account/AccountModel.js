Ext.define('Admin.model.account.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore', 'lastModified',
        'hasPhoto', 'type', 'builtIn', 'editable'
    ],

    idProperty: 'key'
});
