Ext.define('Admin.view.account.DeleteAccountWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteAccountWindow',

    dialogTitle: 'Delete Account(s)',

    items: [
        {
            margin: '10px 0 10px 0px',
            xtype: 'container',
            defaults: {
                xtype: 'button',
                scale: 'medium',
                margin: '0 10 0 0'
            },
            items: [
                {
                    text: 'Delete',
                    iconCls: 'icon-delete-user-24',
                    itemId: 'deleteAccountButton',
                    action: 'deleteAccounts'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    doShow: function (selection) {
        this.setDeleteKeys(selection);
        if (selection.length === 1) {
            this.setDialogInfoTpl(Templates.common.userInfo);
            this.callParent([selection[0]]);
        } else {
            this.setDialogInfoTpl(Templates.account.deleteManyUsers);
            this.callParent(
                [
                    {
                        data: {
                            selection: selection,
                            selectionLength: selection.length
                        }
                    }
                ]
            );
        }
    },

    setDeleteKeys: function (accounts) {
        this.keys = Ext.Array.map(accounts, function (item) {
            return item.data.new_key;
        });
    },

    getDeleteKeys: function () {
        return this.keys;
    }

});