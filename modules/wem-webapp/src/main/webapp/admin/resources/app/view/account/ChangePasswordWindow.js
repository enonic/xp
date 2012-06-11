Ext.define( 'Admin.view.account.ChangePasswordWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.userChangePasswordWindow',

    requires: ['Admin.view.account.DoublePasswordField'],
    dialogTitle: 'Change Password',


    initComponent: function()
    {
        var me = this;

        var changePasswordButton = Ext.create( 'Ext.button.Button', {
            scale: 'medium',
            margin: '0 10 0 0',
            text: 'Change Password',
            iconCls: 'icon-btn-tick-24',
            itemId: 'changePasswordButton',
            disabled: true,
            handler: function() {
                var parentApp = parent.mainApp;
                var form = Ext.getCmp( 'userChangePasswordForm' ).getForm();
                if ( form.isValid() ) {
                    form.submit( {
                        params: {
                            userKey: me.modelData.key
                        },
                        success: function( form, action ) {
                            me.close();
                            if ( parentApp )
                            {
                                parentApp.fireEvent( 'notifier.show', "Password was changed", "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts.");
                            }
                        },
                        failure: function( form, action ) {
                            Ext.Msg.alert('Failed', action.result.msg);
                        }
                    } );
                }
            }
        } );

        var doublePassword = Ext.create( 'Admin.view.account.DoublePasswordField', {
            passwordLabel: 'New password',
            passwordName: 'newPassword',
            repeatLabel: 'Confirm Password',
            repeatName: 'newPassword2',
            listeners: {
                validitychange: function( field, isValid ) {
                    changePasswordButton.setDisabled( !isValid );
                }
            }
        });

        this.items = [
            {
                xtype: 'form',
                id: 'userChangePasswordForm',
                method: 'POST',
                url: 'data/user/changepassword',
                bodyPadding: '10 0',
                bodyCls: 'cms-no-border',
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

        this.callParent( arguments );

        this.on( 'show', function() {
            doublePassword.reset();
        } );
    }

} );
