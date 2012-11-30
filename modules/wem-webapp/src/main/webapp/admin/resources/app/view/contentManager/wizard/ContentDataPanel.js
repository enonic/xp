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

        Ext.each(this.contentType.items, function (contentTypeItem) {
            var widgetItemType = me.parseItemType(contentTypeItem);
            if (!widgetItemType) {
                console.log('Unsupported input type', contentTypeItem);
                return;
            }
            var item = Ext.create({
                xclass: "widget." + widgetItemType,
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

        this.items = [fieldSet];

        this.callParent(arguments);
    },

    parseItemType: function (contentItem) {
        var baseType;
        for (baseType in contentItem) {
            if (contentItem.hasOwnProperty(baseType) && contentItem[baseType].type) {
                return this.typeMapping[contentItem[baseType].type.name];
            }
        }
        return null;
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});