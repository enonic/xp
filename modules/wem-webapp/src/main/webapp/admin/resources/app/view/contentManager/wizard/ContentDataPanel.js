Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    typeMapping: {
        TextLine: "textfield",
        TextArea: "textarea"
    },

    contentItems: [],

    initComponent: function () {
        var me = this;
        var fieldSet = {
            xtype: 'fieldset',
            title: 'A Separator',
            padding: '10px 15px',
            items: []
        };
        me.items = [fieldSet];
        Ext.each(this.contentItems, function (contentItem) {
            var item = Ext.create({
                xclass: "widget." + me.parseItemType(contentItem),
                fieldLabel: contentItem.label,
                name: contentItem.name,
                itemId: contentItem.name,
                cls: 'span-3',
                listeners: {
                    render: function (cmp) {
                        Ext.tip.QuickTipManager.register({
                            target: cmp.el,
                            text: contentItem.helpText
                        });
                    }
                }
            });

            fieldSet.items.push(item);
        });

        me.callParent(arguments);
    },

    parseItemType: function (contentItem) {
        return this.typeMapping[contentItem.inputType.name];
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});