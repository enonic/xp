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
        // set values in fields if editing existing content
        var editedContentValues = {};
        if (this.content && this.content.data) {
            Ext.each(this.content.data, function (dataItem) {
                editedContentValues[dataItem.name] = dataItem.value;
            });
        }

        Ext.each(this.contentType.form, function (contentTypeItem) {
            var widgetItemType = me.parseItemType(contentTypeItem);
            var contentTypeItemData = me.parseItemTypeData(contentTypeItem);
            if (!widgetItemType) {
                console.log('Unsupported input type', contentTypeItem);
                return;
            }
            var item = Ext.create({
                xclass: "widget." + widgetItemType,
                fieldLabel: contentTypeItemData.label,
                name: contentTypeItemData.name,
                itemId: contentTypeItemData.name,
                cls: 'span-3',
                listeners: {
                    render: function (cmp) {
                        Ext.tip.QuickTipManager.register({
                            target: cmp.el,
                            text: contentTypeItemData.helpText
                        });
                    }
                },
                value: editedContentValues[contentTypeItemData.name]
            });

            fieldSet.items.push(item);
        });

        this.items = [fieldSet];

        this.callParent(arguments);
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