Ext.define('Admin.view.datadesigner.wizard.GeneralPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.dataDesignerWizardGeneralPanel',

    initComponent: function () {
        var me = this;

        console.log(this.modelData)
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
                        value: me.modelData ? me.modelData.name : '',
                        name: 'name',
                        itemId: 'name',
                        enableKeyEvents: true
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Module <span style="color: red;">*</span>',
                        allowBlank: false,
                        value: me.modelData ? me.modelData.module : '',
                        name: 'module',
                        itemId: 'module',
                        enableKeyEvents: true
                    }
                ]
            }
        ];

        me.callParent(arguments);
    },

    getData: function () {
        var form = this.getForm();
        var name = form.findField('name').getValue();
        var module = form.findField('module').getValue();

        var data = {
            'name': name,
            'module': module
        };
        return data;
    }
});
