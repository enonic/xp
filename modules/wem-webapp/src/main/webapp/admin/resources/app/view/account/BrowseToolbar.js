Ext.define('Admin.view.account.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',

    border: true,
    cls: 'admin-toolbar',
    defaults: {
        scale: 'medium',
        iconAlign: 'top',
        minWidth: 64
    },

    initComponent: function () {

        this.items = [

            {
                xtype: 'splitbutton',
                text: ' New',
                itemId: 'newAccountButton',
                action: 'newUser',
//                        iconCls: 'icon-add-24',
                cls: 'x-btn-as-arrow',
                menu: Ext.create('Admin.view.MegaMenu', {
                    recentCount: 0,
                    url: 'resources/data/accountMenu.json'
                })
            },
            {
                text: 'Edit',
                action: 'editAccount'
//                        iconCls: 'icon-edit-generic'
            },
            {
                text: 'Delete',
                action: 'deleteAccount',
//                        iconCls: 'icon-delete-user-24',
                disableOnMultipleSelection: false
            },
            {
                text: 'Change Password',
                action: 'changePassword',
//                        iconCls: 'icon-change-password-24',
                disableOnMultipleSelection: true
            },
            {
                text: 'View',
                action: 'viewAccount'
//                        iconCls: 'icon-view-24'
            },
            {
                text: 'Export',
                action: 'exportAccounts',
//                        iconCls: 'icon-export-24',
                disableOnMultipleSelection: false
            }
        ];

        this.callParent(arguments);
    }

});
