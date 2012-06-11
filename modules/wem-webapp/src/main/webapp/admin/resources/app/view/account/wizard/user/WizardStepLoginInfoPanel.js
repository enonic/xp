Ext.define( 'Admin.view.account.wizard.user.WizardStepLoginInfoPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardStepLoginInfoPanel',

    requires: [
        'Admin.view.account.UserFormField'
    ],

    defaults: {
        padding: '10px 15px'
    },

    items: [
        {
            xtype: 'fieldset',
            title: 'Names',
            defaults: {
                xtype: 'textfield',
                allowBlank: false
            },
            items: [
                {
                    fieldLabel: 'Username',
                    name: 'username',
                    emptyText: 'Name',
                    value: 'Suggested name'
                },
                {
                    fieldLabel: 'E-mail',
                    name: 'email',
                    vtype: 'email',
                    emptyText: 'Email'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Security',
            defaults: {
                xtype: 'textfield',
                allowBlank: false
            },
            items: [
                {
                    inputType: 'password',
                    fieldLabel: 'Password',
                    name: 'password',
                    emptyText: 'Password'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Location',
            items: [
                {
                    xtype: 'userFormField',
                    type: 'combo',
                    queryMode: 'local',
                    minChars: 1,
                    emptyText: 'Please select',
                    fieldStore: Ext.data.StoreManager.lookup( 'Admin.store.account.CountryStore' ),
                    valueField: 'code',
                    displayField: 'englishName'
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );
