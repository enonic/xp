Ext.define('Admin.model.userstore.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    idProperty: 'name',

    fields: [
        'name',
        {name: 'defaultStore', type: 'boolean', defaultValue: false },
        'connectorName',
        'configXML',
        { name: 'lastModified', type: 'date', defaultValue: new Date() }
    ]
});
