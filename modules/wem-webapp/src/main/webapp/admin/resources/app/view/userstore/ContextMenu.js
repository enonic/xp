Ext.define('Admin.view.userstore.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.userstoreContextMenu',

    items: [
        {
            text: 'Edit Userstore',
            iconCls: 'icon-edit',
            action: 'editUserstore'
        },
        {
            text: 'Delete Userstore',
            iconCls: 'icon-delete',
            action: 'deleteUserstore'
        },
        '-',
        {
            text: 'Synchronize',
            iconCls: 'icon-refresh',
            action: 'syncUserstore'
        }
    ]
});

