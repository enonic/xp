Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    typeMapping: {
        TextLine: "textfield",
        TextArea: "textarea"
    },

    contentType: undefined,
    content: null, // content to be edited

    initComponent: function () {
        var me = this;

        var fieldSet = {
            xtype: 'fieldset',
            title: 'A Separator',
            padding: '10px 15px',
            items: []
        };
        var dataNamesAdded = {};
        // set values in fields if editing existing content
        var editedContentValues = {};
        if (this.content && this.content.data) {
            Ext.each(this.content.data, function (dataItem) {
                editedContentValues[dataItem.name] = dataItem.value;
            });
        }

        // add component fields defined in content type
        Ext.each(this.contentType.form, function (contentTypeItem) {
            var widgetItemType = me.parseItemType(contentTypeItem);
            var contentTypeInput = me.parseItemTypeData(contentTypeItem);
            if (!widgetItemType) {
                console.log('Unsupported input type', contentTypeItem);
                return;
            }
            var currentValue = editedContentValues[contentTypeInput.name];
            var inputComponent = me.createInputComponent(widgetItemType, contentTypeInput.name, contentTypeInput.label,
                contentTypeInput.helpText, currentValue);
            fieldSet.items.push(inputComponent);
            dataNamesAdded[contentTypeInput.name] = true;
        });

        // add component fields for data items not defined in content type
        if (this.content) { // in edit mode only (not for new)
            Ext.each(this.content.data, function (dataItem) {
                if (!dataNamesAdded[dataItem.name]) {
                    var inputComponent = me.createInputComponent('textfield', dataItem.name, dataItem.name, '', dataItem.value);
                    fieldSet.items.push(inputComponent);
                }
            });
        }

        this.items = [fieldSet];

        this.callParent(arguments);
    },

    createInputComponent: function (widgetItemType, inputName, label, helpText, dataValue) {
        return Ext.create({
            xclass: "widget." + widgetItemType,
            fieldLabel: label,
            name: inputName,
            itemId: inputName,
            cls: 'span-3',
            listeners: {
                render: function (cmp) {
                    Ext.tip.QuickTipManager.register({
                        target: cmp.el,
                        text: helpText
                    });
                }
            },
            value: dataValue
        });
    },

    parseItemType: function (contentItem) {
        var contentItemData = this.parseItemTypeData(contentItem);
        if (contentItemData.type) {
            return this.typeMapping[contentItemData.type.name];
        }
        return null;
    },

    parseItemTypeData: function (contentItem) {
        var baseType;
        for (baseType in contentItem) {
            if (contentItem.hasOwnProperty(baseType)) {
                return contentItem[baseType];
            }
        }
        return null;
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});