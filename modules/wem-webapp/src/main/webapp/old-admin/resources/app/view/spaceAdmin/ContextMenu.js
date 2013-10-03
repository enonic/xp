Ext.define('Admin.view.spaceAdmin.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.spaceContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editSpace'
        },
        {
            text: 'Open',
            iconCls: 'icon-view',
            action: 'viewSpace'
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteSpace'
        }
    ]
});

