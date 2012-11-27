Ext.define('Admin.model.datadesigner.ContentTypeModel', {
    extend: 'Ext.data.Model',

    fields: [
        'qualifiedName',
        'name',
        'module',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() }
    ],

    idProperty: 'qualifiedName'
});