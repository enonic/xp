Ext.define('ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',
    typeMapping: {
        TextLine: "textfield",
        TextArea: "textarea"
    },

    buttons: [{
        text: 'Submit',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        handler: function() {
            var formPanel = this.up('form');
            var form = formPanel.getForm();
            if (form.isValid()) {
                alert(JSON.stringify(formPanel.getData(), null, 4));
                // TODO: Submit form
            }
        }
    }],

    buttonAlign: 'left',

    contentTypeItems: [],
    content: null, // content to be edited

    initComponent: function () {
        var me = this;
        var fieldSet = {
            xtype: 'fieldset',
            title: 'A Separator',
            padding: '10px 15px',
            items: []
        };
        // set values in fields if editing existing content
        var editedContentValues = {};
        if (this.content && this.content.data) {
            Ext.each(this.content.data, function (dataItem) {
                editedContentValues[dataItem.name] = dataItem.value;
            });
        }
        me.items = [fieldSet];

        Ext.each(this.contentTypeItems, function (contentTypeItem) {
            var inputTypeName = contentTypeItem.inputType.name;
            var widgetAlias = 'widget.input.' + inputTypeName;

            // TODO: Should we avoid this magic?
            if (!me.inputIsSupported(widgetAlias)) {
                console.error('Unsupported input type', contentTypeItem);
                return;
            }

            var item = Ext.create({
                xclass: widgetAlias,
                fieldLabel: contentTypeItem.label,
                name: contentTypeItem.name,
                itemId: contentTypeItem.name,
                cls: 'span-3',
                listeners: {
                    render: function (cmp) {
                        Ext.tip.QuickTipManager.register({
                            target: cmp.el,
                            text: contentTypeItem.helpText
                        });
                    }
                },
                value: editedContentValues[contentTypeItem.name]
            });

            fieldSet.items.push(item);
        });

        me.callParent(arguments);
    },

    inputIsSupported: function (alias) {
        return Ext.ClassManager.getByAlias(alias);
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});