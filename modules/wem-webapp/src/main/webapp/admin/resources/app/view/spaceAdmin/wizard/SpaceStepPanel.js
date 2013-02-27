Ext.define('Admin.view.spaceAdmin.wizard.SpaceStepPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.spaceStepPanel',

    stepTitle: 'Space',

    initComponent: function () {
        var displayName = this.modelData && this.modelData.displayName || "";
        var spaceName = this.modelData && this.modelData.name || "";
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
                        fieldLabel: 'Display Name',
                        name: 'displayName',
                        value: displayName
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Name',
                        name: 'spaceName',
                        value: spaceName
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
    },

    getData: function () {
        var form = this.getForm();
        var displayNameValue = form.findField('displayName').getValue();
        var spaceNameValue = form.findField('spaceName').getValue();
        var data = {
            'displayName': displayNameValue,
            'spaceName': spaceNameValue
        };
        return data;
    }
})