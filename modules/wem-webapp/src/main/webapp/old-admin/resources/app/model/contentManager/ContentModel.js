Ext.define('Admin.model.contentManager.ContentModel', {
    extend: 'Ext.data.Model',
    requires: ['Ext.data.UuidGenerator'],

    fields: [
        'id', 'path', 'name', 'type', 'displayName', 'owner', 'modifier', 'iconUrl',
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'editable', type: 'boolean' },
        { name: 'deletable', type: 'boolean' },
        { name: 'allowsChildren', type: 'boolean' },
        { name: 'hasChildren', type: 'boolean' },
        {
            name: 'leaf', // property needed for ContentTreeStore
            type: 'boolean',
            convert: function (value, record) {
                return !record.get('hasChildren');
            }
        }
    ],

    /**
     * TODO[RYA]: WARNING: 'idProperty' - Front-end usage only !!! No Back-end relation !!!
     * No persistent field is used but Ext generated id due to cases exist when items with the same id are appeared in DOM
     * In other models persistent ids still used.
     * Should be changed in case 'Search' done in App with solution that search result must be shown as tree with enclosed nodes.
     * Changes here has been made in scope of [CMS-1302]
     */
    idProperty: 'uuid'
});
