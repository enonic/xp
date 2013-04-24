Ext.define('Admin.view.wizard.SpaceStepPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.spaceStepPanel',

    stepTitle: 'Space',
    data: undefined,

    initComponent: function () {
        var templates = Ext.create('Ext.data.Store', {
            fields: ['code', 'name'],
            data: [
                {"code": "1", "name": "Tpl1"},
                {"code": "2", "name": "Tpl2"},
                {"code": "3", "name": "Tpl3"}
            ]
        });
        this.items = [
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
                        fieldLabel: 'Space Template',
                        displayField: 'name',
                        valueField: 'code',
                        store: templates
                    }
                ]
            }

        ];
        this.callParent(arguments);
    }
});