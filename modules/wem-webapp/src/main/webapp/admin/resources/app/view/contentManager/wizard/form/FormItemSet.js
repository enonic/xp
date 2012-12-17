Ext.define('Admin.view.contentManager.wizard.form.FormItemSet', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.FormItemSet',

    requires: [
        'Admin.lib.Sortable'
    ],

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    formItemSetConfig: undefined,

    content: null, // Blocks

    isCollapsed: false,

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


    /**
     * @private
     */
    initLayout: function () {
        var me = this;

        if (me.content) {
            for (var i = 0; i < me.content.value.length; i++) {
                me.addBlockAt(i, false);
            }

        } else {
            // Remove if test?
            if (me.formItemSetConfig.items) {
                me.addBlockAt(0, true);
            }
        }

        var addBlockButton = {
            xtype: 'button',
            text: 'Add',
            handler: function (button) {
                if (me.formItemSetConfig.items) {
                    me.addBlockAt((me.items.items.length - 1), true);
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
                    html: '<span class="admin-text-button admin-collapse-all-button" href="javascript:;">Collapse All</span>'
                }
            ],
            listeners: {
                render: function (container) {
                    var collapseAllButton = Ext.DomQuery.selectNode('.admin-collapse-all-button', container.getEl().dom);
                    Ext.fly(collapseAllButton).on('click', function (event) {
                        if (me.isCollapsed) {
                            this.setHTML('Collapse All');
                            me.isCollapsed = false;
                        } else {
                            this.setHTML('Expand All');
                            me.isCollapsed = true;
                        }
                    });
                }
            }
        };

        me.add(actionsContainer);
    },


    /**
     * @private
     */
    addBlockAt: function (position, createBlankBlock) {
        var me = this;

        var block = new Ext.container.Container({
            cls: 'admin-sortable admin-formitemset-block',
            formItemSetBlock: true,
            margin: '5 0',
            padding: '0 0 10 0',
            defaults: {
                margin: '5 10'
            },
            items: me.createBlockHeader()
        });

        // Rename argument createBlankBlock. hasContent
        if (createBlankBlock) {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.formItemSetConfig.items, block);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content.value[position], me.formItemSetConfig.items, block);
        }

        me.insert(position, block);
    },


    /**
     * @private
     */
    createBlockHeader: function () {
        var me = this;

        return {
            xtype: 'container',
            margin: '0 0 15 0',
            padding: 5,
            cls: 'header',
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
                    html: '<span class="admin-drag-handle" style="display: inline-block">[#]</span>'
                },
                {
                    xtype: 'component',
                    html: '<h6>' + (me.formItemSetConfig.label || '{No label}') + ': </h6>'
                },
                {
                    tdAttrs: {
                        align: 'right'
                    },
                    xtype: 'button',
                    ui: 'grey',
                    itemId: 'remove-block-button',
                    text: 'x',
                    handler: function (btn) {
                        btn.up().up().destroy();
                    }
                }
            ]
        }
    },


    /**
     * @private
     */
    enableDisableRemoveBlockButton: function () {
        var me = this,
            disable = ((me.items.items.length - 1) === 1),
            button = Ext.ComponentQuery.query('#remove-block-button', me)[0];

        button.setDisabled(disable);
    },


    /**
     * @private
     */
    initSortable: function () {
        new Admin.lib.Sortable(this, {
            proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
            handle: '.admin-drag-handle'
        });
    }

});