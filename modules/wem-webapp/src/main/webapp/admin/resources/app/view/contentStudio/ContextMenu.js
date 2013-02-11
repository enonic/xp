Ext.define('Admin.view.contentStudio.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentStudioContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editContentType'
        },
        {
            text: 'Open',
            iconCls: 'icon-view',
            action: 'viewContentType'
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteContentType'
        }
    ]
});

