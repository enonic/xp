Ext.define('Admin.view.userstore.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.userstoreContextMenu',

    items: [
        {
            text: 'Edit Userstore',
            iconCls: 'icon-edit',
            action: 'editUserstore'
        },
        {
            text: 'View Userstore',
            iconCls: 'icon-view',
            action: 'viewUserstore'
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

