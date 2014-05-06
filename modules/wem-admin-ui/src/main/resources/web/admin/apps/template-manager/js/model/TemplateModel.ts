Ext.define('Admin.model.templateManager.TemplateModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'key',
        'displayName',
        'name',
        'templateType',
        'contentFilter',
        'deletable',
        'editable',
        { name: 'hasChildren', type: 'boolean', defaultValue: false },
        {
            name: 'leaf', // property needed for TemplateTreeStore
            type: 'boolean',
            convert: function (value, record) {
                return !record.get('hasChildren');
            }
        }
    ],

    idProperty: 'key'
});