Ext.define('Admin.view.account.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',

    border: false,

    initComponent: function () {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        xtype: 'splitbutton',
                        text: ' New',
                        itemId: 'newAccountButton',
                        action: 'newUser',
                        iconCls: 'icon-add-24',
                        cls: 'x-btn-as-arrow',
                        menu: Ext.create('Admin.view.MegaMenu', {
                            recentCount: 0,
                            url: 'resources/data/accountMenu.json'
                        })
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 3,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Edit',
                        action: 'editAccount',
                        iconCls: 'icon-edit-generic'
                    },
                    {
                        text: 'Delete',
                        action: 'deleteAccount',
                        iconCls: 'icon-delete-user-24',
                        disableOnMultipleSelection: false
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Change Password',
                        action: 'changePassword',
                        iconCls: 'icon-change-password-24',
                        disableOnMultipleSelection: true
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'View',
                        action: 'viewAccount',
                        iconCls: 'icon-view-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Export',
                        action: 'exportAccounts',
                        iconCls: 'icon-export-24',
                        disableOnMultipleSelection: false
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});
