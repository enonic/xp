Ext.define('Admin.model.account.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key',
        'name',
        'email',
        'qualifiedName',
        'displayName',
        'userStore',
        'type',
        'image_url',
        { name: 'builtIn', type: 'boolean' },
        { name: 'editable', type: 'boolean' },
        { name: 'deleted', type: 'boolean' },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        { name: 'createdTime', type: 'date', defaultValue: new Date() }
    ],

    idProperty: 'key'
});
