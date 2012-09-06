Ext.define('Admin.view.account.ChangePasswordWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.userChangePasswordWindow',

    requires: ['Admin.view.account.DoublePasswordField'],
    dialogTitle: 'Change Password',


    initComponent: function () {
        var me = this;

        var changePasswordButton = Ext.create('Ext.button.Button', {
            scale: 'medium',
            margin: '0 10 0 0',
            text: 'Change Password',
            iconCls: 'icon-btn-tick-24',
            itemId: 'changePasswordButton',
            disabled: true,
            handler: function () {
                var parentApp = parent.mainApp;
                var form = Ext.getCmp('userChangePasswordForm').getForm();
                var accountKey = me.modelData.new_key;
                var pwdValue = form.items[0].getValue();
                if (form.isValid()) {
                    Admin.lib.RemoteService.account_changePassword({ key: accountKey, password: pwdValue }, function (response) {
                        if (response.success) {
                            me.close();
                            if (parentApp) {
                                parentApp.fireEvent('notifier.show', "Password was changed",
                                    "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts.");
                            }
                        } else {
                            Ext.Msg.alert('Failed', response.error);
                        }
                    });
                }
            }
        });

        var doublePassword = Ext.create('Admin.view.account.DoublePasswordField', {
            passwordLabel: 'New password',
            passwordName: 'newPassword',
            repeatLabel: 'Confirm Password',
            repeatName: 'newPassword2',
            listeners: {
                validitychange: function (field, isValid) {
                    changePasswordButton.setDisabled(!isValid);
                }
            }
        });

        this.items = [
            {
                xtype: 'form',
                id: 'userChangePasswordForm',
                method: 'POST',
                bodyPadding: '10 0',
                bodyCls: 'admin-no-border',
                layout: 'anchor',
                defaults: {
                    allowBlank: false
                },
                items: [
                    doublePassword,
                    {
                        margin: '0 0 10px 105px',
                        xtype: 'container',
                        items: [
                            changePasswordButton
                        ]
                    }
                ]
            }
        ];

        this.callParent(arguments);

        this.on('show', function () {
            doublePassword.reset();
        });
    }

});
