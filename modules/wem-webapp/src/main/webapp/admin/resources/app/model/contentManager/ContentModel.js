Ext.define( 'Admin.model.contentManager.ContentModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'type', 'owner', 'lastModified'
    ],

    idProperty: 'key'
} );
