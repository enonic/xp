Ext.define('Admin.view.spaceAdmin.wizard.SpaceStepPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.spaceStepPanel',

    stepTitle: 'Space',

    initComponent: function () {

        this.items = [
            {
                xtype: 'fieldset',
                title: 'Name',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Display Name'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Name'
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Template',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'combo',
                        fieldLabel: 'Space Template'
                    }
                ]
            }

        ];
        this.callParent(arguments);
    }
})