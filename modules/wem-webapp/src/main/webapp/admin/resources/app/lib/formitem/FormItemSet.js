Ext.define('Admin.lib.formitem.FormItemSet', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.FormItemSet',

    requires: [
        'Admin.lib.Sortable'
    ],

    mixins: {
        formHelper: 'Admin.lib.formitem.FormHelper'
    },

    formItemSetConfig: undefined,

    fieldLabel: '',
    margin: '10 0 10 0',

    listeners: {
        render: function () {
            this.initLayout();
            this.initSortable();
        },
        afterlayout: function () {
            this.enableDisableRemoveBlockButton();
        }
    },


    initComponent: function () {
        this.callParent(arguments);
    },


    insertBlockAt: function (position) {
        var me = this;
        console.log('FormItemSet: insertBlockAt  ' + position);

        var block = me.createBlock();

        me.mixins.formHelper.addFormItems(me.formItemSetConfig.items, block);

        me.insert(position, block);
    },


    createBlock: function () {
        var me = this;

        var block = new Ext.form.FieldContainer({
            cls: 'admin-sortable admin-formitemset-block',
            style: 'border:none',
            margin: '0 0 10 0',
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
                            html: '<span class="admin-draghandle" style="display: inline-block">[dragger]</span>'
                        },
                        {
                            xtype: 'component',
                            html: '<h6>' + me.formItemSetConfig.label + '</h6>'
                        },
                        {
                            tdAttrs: {
                                align: 'right'
                            },
                            xtype: 'button',
                            itemId: 'remove-button',
                            text: 'x',
                            handler: function () {
                                block.destroy();
                            }
                        }
                    ]
                }
            ]
        });

        return block;
    },


    enableDisableRemoveBlockButton: function () {
        var me = this;
        var disable = ((me.items.items.length - 1) === 1);
        var button = Ext.ComponentQuery.query('#remove-button', me)[0];
        button.setDisabled(disable);
    },


    initLayout: function () {
        var me = this;
        if (me.formItemSetConfig.items) {
            me.insertBlockAt(0);
        }

        var addBlockButton = {
            xtype: 'button',
            text: '+',
            handler: function (button) {
                if (me.formItemSetConfig.items) {
                    me.insertBlockAt((me.items.items.length - 1));
                }
            }
        };

        var actionsContainer = {
            xtype: 'container',
            layout: {
                type: 'table',
                columns: 2,
                tableAttrs: {
                    style: 'width: 100%'
                }
            },
            items: [
                addBlockButton,
                {
                    tdAttrs: {
                        align: 'right',
                        valign: 'top'
                    },
                    xtype: 'component',
                    html: '<span class="admin-text-button" href="javascript:;">Collapse all</span>'
                }
            ]
        };

        me.add(actionsContainer);
    },

    initSortable: function () {
        new Admin.lib.Sortable(this, 'formItemSetDragGroup' + Ext.id(), {
            proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
            handle: '.admin-draghandle'
        });
    }

});