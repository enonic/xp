Ext.define('ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    contentTypeItems: [],
    content: null, // content to be edited

    autoDestroy: true,

    // Temporary form submit button
    buttonAlign: 'left',
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
            me.insertFormItemSetBlock(formItemSet, 0);
        }

        return formItemSet;
    },


    insertFormItemSetBlock: function (formItemSet, position) {
        var me = this;

        // May use regular container here
        var block = new Ext.form.FieldContainer({
            //style: 'border-bottom: 1px solid #aaa;',
            layout: 'anchor'
        });

        me.addFormFields(formItemSet.contentTypeItems, block);

        // Add and remove buttons
        block.add({
            xtype: 'container',
            margin: '10 0 5 0',
            padding: '0 0 0 0',
            style: 'text-align: right; border-top: 1px dotted #aaa',

            defaults: {
            },

            items: [
                {
                    xtype: 'button',
                    text: '+',
                    margin: '0 5 0 0',
                    handler: function (button) {
                        if (me.contentTypeItems) {
                            var pos = me.getPositionForFormItemSetBlock(formItemSet, block) + 1;
                            me.insertFormItemSetBlock(formItemSet, pos);
                        }
                    }
                },
                {
                    xtype: 'button',
                    text: '-',
                    handler: function (button) {
                        block.destroy();
                    }
                }
            ]
        });

        formItemSet.insert(position, block);
    },


    getPositionForFormItemSetBlock: function (formItemSet, block) {
        return formItemSet.items.indexOf(block)
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