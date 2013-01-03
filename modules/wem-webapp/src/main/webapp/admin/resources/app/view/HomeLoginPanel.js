Ext.define('Admin.view.HomeLoginPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.homeLogin',
    renderTo: Ext.getBody(),
    cls: 'admin-home-login-panel',
    border: false,

    initComponent: function () {
        var me = this;

        var userStoresStore = Ext.create('Ext.data.Store', {
            fields: ['key', 'name', 'default'],
            data: [
                {'key': '1', 'name': 'ABC', default: false},
                {'key': '2', 'name': 'LDAP', default: true},
                {'key': '3', 'name': 'Local', default: false},
                {'key': '4', 'name': 'Some very long value', default: false}
            ]
        });

        var defaultUserStore = userStoresStore.findRecord('default', true).raw.key;

        this.defaults = {
            margin: '0 0 10 0'
        };
        this.items = [
            {
                xtype: 'component',
                autoEl: {
                    tag: 'h3'
                },
                html: 'Login'
            },
            {
                xtype: 'combo',
                name: 'userstore',
                id: 'userstoreCombo',
                allowBlank: false,
                store: userStoresStore,
                fieldLabel: '',
                labelWidth: 1,
                labelCls: 'combo-field-label',
                queryMode: 'local',
                displayField: 'name',
                valueField: 'key',
                width: 200,
                tabIndex: 1,
                listeners: {
                    render: function (combo) {
                        combo.setValue(defaultUserStore);
                    }
                }
            },
            {
                xtype: 'textfield',
                name: 'userid',
                allowBlank: false,
                emptyText: 'userid or e-mail',
                width: 200,
                tabIndex: 2
            },
            {
                xtype: 'textfield',
                name: 'password',
                allowBlank: false,
                inputType: 'password',
                emptyText: 'Password',
                width: 200,
                tabIndex: 3
            },
            {
                xtype: 'button',
                formBind: true,
                disabled: true,
                colspan: 2,
                text: 'Log In',
                tabIndex: 4,
                handler: function (button) {
                    //var form = this.up('form').getForm();
                    //form.submit();
                }
            }
        ];

        this.callParent(arguments);

    }

});
