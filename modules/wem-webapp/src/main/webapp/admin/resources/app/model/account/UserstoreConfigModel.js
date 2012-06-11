Ext.define('Admin.model.account.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        'key',
        'name',
        { name: 'defaultStore', type: 'boolean', defaultValue: false },
        'connectorName',
        'configXML',
        { name: 'lastModified', type: 'date', defaultValue: new Date() }
    ]
});
