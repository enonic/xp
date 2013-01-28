Ext.define('Admin.view.homescreen.LoginPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.loginPanel',

    frame: false,
    border: false,
    bodyStyle: 'background:transparent;',

    renderTo: 'admin-home-login-form-container',

    initComponent: function () {
        var me = this;

        /*
            Inline the userstore store for now
        */
        me.userStoresStore = Ext.create('Ext.data.Store', {
            fields: ['key', 'name', 'default'],
            data: [
                {'key': '1', 'name': 'ABC', default: false},
                {'key': '2', 'name': 'LDAP', default: true},
                {'key': '3', 'name': 'Local', default: false},
                {'key': '4', 'name': 'Some very long value', default: false}
            ]
        });

        me.items = [
            {
                xtype: 'container',
                html: '<h3>Login</h3>'
            },
            {
                xtype: 'combo',
                name: 'userstore',
                id: 'userstoreCombo',
                itemId: 'userstoreCombo',
                allowBlank: false,
                enableKeyEvents: true,
                store: me.userStoresStore,
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
                        combo.setValue(me.getDefaultUserStore().raw.key);
                    }
                }
            },
            {
                xtype: 'textfield',
                name: 'userid',
                itemId: 'userId',
                allowBlank: false,
                enableKeyEvents: true,
                emptyText: 'userid or e-mail',
                width: 200,
                tabIndex: 2
            },
            {
                xtype: 'textfield',
                name: 'password',
                itemId: 'password',
                allowBlank: false,
                enableKeyEvents: true,
                inputType: 'password',
                emptyText: 'password',
                width: 200,
                tabIndex: 3
            },
            {
                xtype: 'button',
                type: 'submit',
                itemId: 'loginButton',
                formBind: true,
                disabled: true,
                colspan: 2,
                style: 'float:right;margin-right:5px',
                text: 'Log In',
                tabIndex: 4
            }
        ];

        me.callParent(arguments);
    },

    getDefaultUserStore: function () {
        return this.userStoresStore.findRecord('default', true);
    }

});
