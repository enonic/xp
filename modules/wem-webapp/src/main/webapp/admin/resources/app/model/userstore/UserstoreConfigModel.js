Ext.define('Admin.model.userstore.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        'key',
        'name',
        {name: 'defaultStore', type: 'boolean', defaultValue: false },
        'connectorName',
        'configXML', 'lastModified'
    ]
});
