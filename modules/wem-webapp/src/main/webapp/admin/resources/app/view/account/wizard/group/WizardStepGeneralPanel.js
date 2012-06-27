Ext.define('Admin.view.account.wizard.group.WizardStepGeneralPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardStepGeneralPanel',


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
                        xtype: 'textfield',
                        fieldLabel: 'Name <span style="color: red;">*</span>',
                        allowBlank: false,
                        value: me.modelData ? me.modelData.displayName : '',
                        name: 'displayName',
                        itemId: 'displayName',
                        enableKeyEvents: true,
                        emptyText: 'Display Name'
                    },
                    {
                        xtype: 'checkbox',
                        fieldLabel: 'Public group',
                        checked: me.modelData ? me.modelData['public'] : false,
                        name: 'public'
                    },
                    {
                        xtype: 'textarea',
                        fieldLabel: 'Description',
                        allowBlank: true,
                        rows: 5,
                        value: me.modelData ? me.modelData.description : '',
                        name: 'description'
                    }
                ]
            }
        ];
        me.callParent(arguments);
    },

    getData: function () {
        var form = this.getForm();
        var displayName = form.findField('displayName').getValue();
        var isPublic = form.findField('public').getValue();
        var description = form.findField('description').getValue();
        var data = {
            'displayName': displayName,
            'public': isPublic,
            'description': description
        };
        return data;
    }
});
