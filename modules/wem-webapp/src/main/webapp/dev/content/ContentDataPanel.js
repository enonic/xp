Ext.define('ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',
    typeMapping: {
        TextLine: "textfield",
        TextArea: "textarea"
    },

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

    initComponent: function () {
        var me = this;
        // set values in fields if editing existing content
        /*
         var editedContentValues = {};
         if (this.content && this.content.data) {
         Ext.each(this.content.data, function (dataItem) {
         editedContentValues[dataItem.name] = dataItem.value;
         });
         }
         */
        me.items = [];

        me.addFormFields(me.contentTypeItems, me);

        me.callParent(arguments);
    },

    addFormFields: function (contentTypeItems, container) {
        var me = this;
        Ext.each(contentTypeItems, function (contentTypeItem) {
            var inputTypeName = contentTypeItem.Input.type.name;
            var xtype = 'widget.input.' + inputTypeName;
            if (!me.formFieldIsSupported(xtype)) {
                console.error('Unsupported input type', contentTypeItem);
                return;
            }

            var item = me.createFormField(contentTypeItem, xtype);
            container.items.push(item);
        });
    },

    createFormField: function (contentTypeItem, widgetAlias) {
        return Ext.create({
            xclass: widgetAlias,
            fieldLabel: contentTypeItem.Input.label,
            name: contentTypeItem.Input.name,
            itemId: contentTypeItem.Input.name,
            cls: 'span-3',
            listeners: {
                render: function (cmp) {
                    Ext.tip.QuickTipManager.register({
                        target: cmp.el,
                        text: contentTypeItem.Input.helpText
                    });
                }
            }
            // value: editedContentValues[contentTypeItem.name]
        });
    },

    formFieldIsSupported: function (xtype) {
        return Ext.ClassManager.getByAlias(xtype);
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});