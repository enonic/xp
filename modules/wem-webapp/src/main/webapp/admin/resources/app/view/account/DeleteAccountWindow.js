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
        var Templates_common_userInfo =
        		'<div>' +
        		    '<div class="admin-user-info clearfix">' +
        		        '<div class="admin-user-photo west admin-left">' +
        		            '<div class="photo-placeholder">' +
        		                '<img src="{[values.image_url]}?size=100" alt="{name}"/>' +
        		            '</div>' +
        		        '</div>' +
        		        '<div class="admin-left">' +
        		            '<h2>{displayName}</h2>({qualifiedName})<br/>' +
        		            '<a href="mailto:{email}:">{email}</a>' +
        		        '</div>' +
        		    '</div>' +
        		'</div>';

        this.setDeleteKeys(selection);
        if (selection.length === 1) {
            this.setDialogInfoTpl(Templates_common_userInfo);
            this.callParent([selection[0]]);
        } else {
            var Templates_account_deleteManyUsers =
        		'<div class="admin-delete-user-confirmation-message">' +
        		    '<div class="icon-question-mark-32 admin-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
        		    '<div class="admin-left" style="margin-top:5px">Are you sure you want to delete the selected {selectionLength} items?</div>' +
        		'</div>';

            this.setDialogInfoTpl(Templates_account_deleteManyUsers);
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
            return item.data.key;
        });
    },

    getDeleteKeys: function () {
        return this.keys;
    }

});