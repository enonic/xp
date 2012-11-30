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

        me.addFormItems(me.contentTypeItems, me);

        me.callParent(arguments);
    },


    addFormItems: function (contentTypeItems, parentContainer) {
        var me = this;
        var formItem;
        Ext.each(contentTypeItems, function (item) {
            if (item.FormItemSet) {
                formItem = me.createFormItemSet(item.FormItemSet);
            } else { // Input
                formItem = me.createItem(item.Input);
            }

            if (parentContainer.getXType() === 'FormItemSet' || parentContainer.getXType() === 'fieldcontainer') {
                parentContainer.add(formItem);
            } else {
                parentContainer.items.push(formItem);
            }
        });
    },


    createFormItemSet: function (formItemSet) {
        var me = this;
        var formItem = Ext.create({
            xclass: 'widget.FormItemSet',
            fieldLabel: formItemSet.label,
            name: formItemSet.name,
            contentTypeItems: formItemSet.items || null

        });
        if (formItem.items) {
            me.addFormItemSetBlock(formItem, 0);
        }

        return formItem;
    },


    createItem: function (input) {
        var me = this;
        var classAlias = 'widget.' + input.type.name;

        if (!me.formItemIsSupported(classAlias)) {
            console.error('Unsupported input type', input);
            return;
        }

        return Ext.create({
            xclass: classAlias,
            fieldLabel: input.label,
            name: input.name
        });
    },


    addFormItemSetBlock: function (formItemSet, position) {
        var me = this;

        // May use regular container here
        var block = new Ext.form.FieldContainer({
            //style: 'border-bottom: 1px solid #aaa;',
            layout: 'anchor'
        });

        me.addFormItems(formItemSet.contentTypeItems, block);

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
                            me.addFormItemSetBlock(formItemSet, pos);
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
        return formItemSet.items.indexOf(block);
    },


    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    },


    getData: function () {
        return this.getForm().getFieldValues();
    }

});