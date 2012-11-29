Ext.define('ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    contentTypeItems: [],
    content: null, // content to be edited

    buttons: [{
        text: 'Submit',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        handler: function() {
            var formPanel = this.up('form');
            var form = formPanel.getForm();
            if (form.isValid()) {
                alert('Create form data and submit');
            }
        }
    }],

    buttonAlign: 'left',

    autoDestroy: true,

    initComponent: function () {
        var me = this;

        me.items = [];

        me.addFormFields(me.contentTypeItems, me);

        me.callParent(arguments);
    },

    addFormFields: function (contentTypeItems, container) {
        var me = this;
        var formField;

        Ext.each(contentTypeItems, function (item) {
            /*
            if (!me.formWidgetIsSupported(widgetName)) {
                console.error('Unsupported input type', contentTypeItem);
                return;
            }
            */

            if (item.FormItemSet) {
                formField = me.createFormItemSet(item.FormItemSet);
            } else { // Input
                formField = me.createInput(item.Input);
            }

            if (container.getXType() === 'input.FormItemSet' || container.getXType() === 'fieldcontainer') {
                container.add(formField);
            } else {
                container.items.push(formField);
            }

        });
    },

    createFormItemSet: function (formItemSetConfig) {
        var me = this;
        var formItemSet = Ext.create({
            xclass: 'widget.input.FormItemSet',
            fieldLabel: formItemSetConfig.label,
            name: formItemSetConfig.name,
            contentTypeItems: formItemSetConfig.items || null

        });
        if (formItemSetConfig.items) {
            me.addFormItemSetBlock(formItemSetConfig.items, formItemSet);
        }

        return formItemSet;
    },

    addFormItemSetBlock: function (formItemSetItemsConfig, formItemSet) {
        var me = this;
        var container = new Ext.form.FieldContainer({
            style: 'border-bottom: 1px solid #aaa;',
            layout: 'anchor'
        });

        me.addFormFields(formItemSetItemsConfig, container);

        container.add({
            xtype: 'button',
            text: '+',
            handler: function (button) {
                if (me.contentTypeItems) {
                    me.addFormItemSetBlock(formItemSet.contentTypeItems, formItemSet);
                }
            }
        });

        formItemSet.add(container);
    },

    createInput: function (inputConfig) {
        var me = this;
        var widget = 'widget.input.' + inputConfig.type.name;

        return Ext.create({
            xclass: widget,
            fieldLabel: inputConfig.label,
            name: inputConfig.name
        });
    },

    formWidgetIsSupported: function (alias) {
        return Ext.ClassManager.getByAlias(alias);
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});