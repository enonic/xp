Ext.define('Admin.model.contentManager.ContentModel', {
    extend: 'Ext.data.Model',

    fields: [
        'path', 'name', 'type', 'displayName', 'owner', 'modifier', 'iconUrl',
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'editable', type: 'boolean' },
        { name: 'deletable', type: 'boolean' },
        { name: 'hasChildren', type: 'boolean' },
        {
            name: 'leaf', // property needed for ContentTreeStore
            type: 'boolean',
            convert: function (value, record) {
                return !record.get('hasChildren');
            }
        }
    ],

    idProperty: 'path'
});
