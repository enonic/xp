Ext.define('Admin.view.account.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.accountContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editAccount',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteAccount'
        },
        {
            text: 'View',
            iconCls: 'icon-view',
            action: 'viewAccount',
            disableOnMultipleSelection: false
        },
        '-',
        {
            text: 'Change Password',
            iconCls: 'icon-change-password',
            action: 'changePassword',
            disableOnMultipleSelection: true
        }
    ]
});

