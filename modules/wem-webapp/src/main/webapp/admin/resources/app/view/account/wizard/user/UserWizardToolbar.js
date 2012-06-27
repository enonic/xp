Ext.define('Admin.view.account.wizard.user.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userWizardToolbar',

    border: false,

    isNewUser: true,

    initComponent: function () {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Save',
                        action: 'saveUser',
                        itemId: 'save',
                        disabled: true,
                        iconCls: 'icon-save-24'
                    }
                ]
            }
        ];

        if (!this.isNewUser) {
            leftGrp.push({
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Delete',
                        action: 'deleteUser',
                        iconCls: 'icon-delete-user-24'
                    }
                ]
            }, {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Change Password',
                        action: 'changePassword',
                        iconCls: 'icon-change-password-24'
                    }
                ]
            });
        }

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closeWizard',
                    iconCls: 'icon-cancel-24'
                }
            ]
        };

        this.items = leftGrp.concat([ '->', rightGrp ]);
        this.callParent(arguments);
    }

});
