Ext.define('Admin.view.datadesigner.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.datadesignerContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editContentType'
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteContentType'
        },
        {
            text: 'View',
            iconCls: 'icon-view',
            action: 'viewContentType'
        }
    ]
});

