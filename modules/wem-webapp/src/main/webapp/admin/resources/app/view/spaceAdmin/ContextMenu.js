Ext.define('Admin.view.spaceAdmin.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.spaceContextMenu',

    cls: 'admin-context-menu',
    border: false,

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

