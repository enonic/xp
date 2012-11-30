Ext.define('Admin.view.contentStudio.wizard.ContentTypePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentStudioWizardContentTypePanel',

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
                    },
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
        var form = this.getForm();
        var type = form.findField('contentType').getValue();
        var name = form.findField('name').getValue();
        var module = form.findField('module').getValue();

        return {
            'type': type,
            'name': name,
            'module': module
        };
    }
});
