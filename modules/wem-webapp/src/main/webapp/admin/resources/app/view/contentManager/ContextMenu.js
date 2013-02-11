Ext.define('Admin.view.contentManager.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentManagerContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Open',
            iconCls: 'icon-view',
            action: 'viewContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteContent'
        }
    ]
});

