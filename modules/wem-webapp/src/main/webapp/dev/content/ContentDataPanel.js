/**
 * TODO: The create and add methods should be moved to a helper class or to the formitem
 */

Ext.define('ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',
    requires: [
        'Admin.lib.Sortable',
        'Admin.lib.formitem.HtmlArea',
        'Admin.lib.formitem.Relation',
        'Admin.lib.formitem.TextArea',
        'Admin.lib.formitem.TextLine'
    ],

    layout: 'vbox',

    contentType: undefined,
    content: null, // content to be edited

    autoDestroy: true,

    initComponent: function () {
        var me = this;
        me.items = [];

        me.addFormItems(me.contentType.form, me);

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

            if (parentContainer.getXType() === 'FormItemSet' || parentContainer.getXType() === 'fieldcontainer') {
                parentContainer.add(formItem);
            } else {
                parentContainer.items.push(formItem);
            }
        });
    },


    createItem: function (inputConfig) {
        var me = this;
        var classAlias = 'widget.' + inputConfig.type.name;

        if (!me.formItemIsSupported(classAlias)) {
            console.error('Unsupported input type', inputConfig);
            return;
        }

        return Ext.create({
            xclass: classAlias,
            fieldLabel: inputConfig.label,
            name: inputConfig.name,
            inputConfig: inputConfig
        });
    },


    createFormItemSet: function (formItemSetConfig) {
        var me = this;
        var formItemSet = Ext.create('Ext.form.FieldContainer', {
            fieldLabel: '',
            name: formItemSetConfig.name,
            inputConfig: formItemSetConfig,
            contentTypeItems: formItemSetConfig.items || null,
            listeners: {
                render: function (component) {
                    new Admin.lib.Sortable(component, 'formItemSetDragGroup' + Ext.id(), {
                        proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
                        handle: '.admin-draghandle'
                    });
                },
                add: function (container) {
                    me.updateRemoveBlockButtonDisabledState(container);
                },
                remove: function (container) {
                    me.updateRemoveBlockButtonDisabledState(container);
                }
            }
        });


        if (formItemSet.items) {
            me.insertFormItemSetBlock(formItemSet, 0);
        }

        var addBlockButton = {
            xtype: 'button',
            text: '+',
            handler: function (button) {
                if (formItemSet.contentTypeItems) {
                    me.insertFormItemSetBlock(formItemSet, (formItemSet.items.items.length - 1));
                }
            }
        };

        formItemSet.add(addBlockButton);

        return formItemSet;
    },


    insertFormItemSetBlock: function (formItemSet, position) {
        var me = this;

        var block = new Ext.form.FieldContainer({
            cls: 'admin-sortable admin-formitemset-block',
            style: 'border:none',
            margin: '0 0 5 0',
            defaults: {
                margin: 5
            },
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'table',
                        columns: 3,
                        tableAttrs: {
                            style: 'width: 100%'
                        }
                    },
                    items: [
                        {
                            tdAttrs: {
                                style: 'width: 30px'
                            },
                            xtype: 'component',
                            html: '<div class="admin-draghandle" style="display: inline-block">Dragger</div>'
                        },
                        {
                            xtype: 'component',
                            html: '<h5>' + formItemSet.inputConfig.label + '</h5>'
                        },
                        {
                            tdAttrs: {
                                align: 'right'
                            },
                            xtype: 'button',
                            itemId: 'remove-button',
                            text: 'X',
                            handler: function () {
                                block.destroy();
                            }
                        }
                    ]
                }
            ]
        });
        me.addFormItems(formItemSet.contentTypeItems, block);

        formItemSet.insert(position, block);
    },


    updateRemoveBlockButtonDisabledState: function (formItemSetContainer) {
        var disable = ((formItemSetContainer.items.items.length - 1) === 1);
        Ext.ComponentQuery.query('#remove-button', formItemSetContainer)[0].setDisabled(disable);
    },


    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    },


    getData: function () {
        return this.getForm().getFieldValues();
    },


    /**  Temporary form submit button (for development only)**/

    buttonAlign: 'left',
    buttons: [
        {
            text: 'Submit',
            formBind: true, //only enabled once the form is valid
            disabled: true,
            handler: function () {
                var formPanel = this.up('form');
                var form = formPanel.getForm();
                if (form.isValid()) {
                    alert('Create form data and submit');
                }
            }
        }
    ]

});