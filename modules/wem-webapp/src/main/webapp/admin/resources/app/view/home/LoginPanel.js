Ext.define('Admin.view.home.LoginPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.homeLoginPanel',

    frame: false,
    border: false,
    bodyStyle: 'background:transparent;',

    renderTo: 'admin-home-login-form-container',

    loggedIn: false,

    initComponent: function () {
        var me = this;

        console.log('init login panel');

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
                allowBlank: false,
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
                        combo.setValue(me.userStoresStore.findRecord('default', true).raw.key);
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
                emptyText: 'password',
                width: 200,
                tabIndex: 3
            },
            {
                xtype: 'button',
                formBind: true,
                disabled: true,
                colspan: 2,
                style: 'float:right;margin-right:5px',
                text: 'Log In',
                tabIndex: 4,
                handler: function (button) {
                    var loginForm = Ext.get('admin-home-login-form'),
                        appSelector = Ext.get('admin-home-app-selector'),
                        openApps = Ext.get('admin-home-app-info-container');

                    loginForm.setVisibilityMode(Ext.Element.OFFSETS);
                    loginForm.animate({
                        duration: 500,
                        to: {
                            opacity: 0
                        },
                        listeners: {
                            afteranimate: function () {
                                loginForm.hide();

                                Ext.getCmp('admin-home-app-selector-search').focus();

                                appSelector.setStyle('visibility', 'visible').addCls('fade-in');
                                openApps.setStyle('visibility', 'visible').addCls('fade-in');

                                me.loggedIn = true;
                            }
                        }
                    });
                }
            }
        ];

        me.callParent(arguments);
    }

});
