Ext.define('Admin.view.schemaManager.wizard.ConfigPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.schemaManagerWizardConfigPanel',
    requires: ['Admin.lib.CodeMirror'],

    initComponent: function () {
        var me = this;
        // codemirror will throw exception if value is undefined
        var configXml = me.data && me.data.get('configXML') || "";
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
                        xtype: 'codemirror',
                        fieldLabel: 'XML',
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
