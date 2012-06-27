Ext.define('Admin.view.datadesigner.wizard.ConfigPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.dataDesignerWizardConfigPanel',

    initComponent: function () {
        var me = this;
        var configXml = me.modelData ? me.modelData.configXML : "";
        me.items = [
            {
                xtype: 'fieldset',
                title: 'Config',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'textarea',
                        fieldLabel: 'XML',
                        height: 400,
                        value: configXml,
                        name: 'configXML'
                    }
                ]
            }
        ];
        me.callParent(arguments);
    },

    getData: function () {
        var form = this.getForm();
        var configXML = form.findField('configXML').getValue();
        var data = {
            'configXML': configXML
        };
        return data;
    }
});
