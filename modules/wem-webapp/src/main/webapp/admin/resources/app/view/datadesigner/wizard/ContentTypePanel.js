Ext.define('Admin.view.datadesigner.wizard.ContentTypePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.dataDesignerWizardContentTypePanel',

    initComponent: function () {
        var me = this;

        var contentTypeValue = me.modelData ? me.modelData.type : undefined;

        me.items = [
            {
                xtype: 'fieldset',
                title: 'Content Type',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'combobox',
                        fieldLabel: 'Content Type<span style="color: red;">*</span>',
                        forceSelection: true,
                        allowBlank: false,
                        value: contentTypeValue,
                        store: me.getContentTypesStore(),
                        queryMode: 'local',
                        displayField: 'name',
                        valueField: 'type',
                        name: 'contentType',
                        disabled: Ext.isDefined(contentTypeValue)
                    }
                ]
            }
        ];

        me.callParent(arguments);
    },

    getContentTypesStore: function () {
        return Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
            data: [
                {"name": 'Base', "type": 'base'},
                {"name": 'Site', "type": 'site'},
                {"name": 'Shortcut', "type": 'shortcut'},
                {"name": 'Folder', "type": 'folder'},
                {"name": 'Structured', "type": 'struct'},
                {"name": 'Media', "type": 'media'},
                {"name": 'Form', "type": 'form'}
            ]
        });
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }
});