Ext.define('Admin.view.userstore.wizard.UserstoreWizardGeneralPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreWizardGeneralPanel',


    initComponent: function () {
        var me = this;
        me.items = [
            {
                xtype: 'fieldset',
                title: 'General',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'hiddenfield',
                        name: 'key',
                        value: me.modelData ? me.modelData.key : ''
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Name <span style="color: red;">*</span>',
                        allowBlank: false,
                        regex: /^[a-zA-Z0-9_\-]+$/,
                        regexText: 'Field has invalid characters',
                        value: me.modelData ? me.modelData.name : '',
                        name: 'displayName',
                        itemId: 'displayName',
                        enableKeyEvents: true,
                        emptyText: 'Display Name'
                    },
                    {
                        xtype: 'combo',
                        allowBlank: false,
                        fieldLabel: 'Connector <span style="color: red;">*</span>',
                        name: 'connectorName',
                        triggerAction: 'all',
                        queryMode: 'local',
                        typeAhead: true,
                        valueField: 'name',
                        displayField: 'name',
                        store: 'Admin.store.userstore.UserstoreConnectorStore',
                        forceSelection: true,
                        listConfig: {
                            getInnerTpl: function () {
                                return '{name} ({pluginType})';
                            }
                        },
                        value: me.modelData ? me.modelData.connectorName : ''
                    }
                ]
            }
        ];
        me.callParent(arguments);
    },

    getData: function () {
        var form = this.getForm();
        var displayName = form.findField('displayName').getValue();
        var connectorName = form.findField('connectorName').getValue();
        var key = form.findField('key').getValue();
        var data = {
            'name': displayName,
            'connectorName': connectorName,
            'key': key
        };
        return data;
    }
});
