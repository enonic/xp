Ext.define('Admin.view.contentManager.wizard.form.FormItemSet', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.FormItemSet',

    requires: [
        'Admin.lib.Sortable'
    ],

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    contentTypeItemConfig: undefined,

    content: null, // Blocks

    isCollapsed: false,

    fieldLabel: '',

    margin: '10 0 10 0',

    initComponent: function () {
        this.callParent(arguments);
    },

    listeners: {
        beforerender: function () {
            this.setIndent();
        },
        render: function () {
            this.initLayout();
            this.initSortable();
        },
        afterlayout: function () {
            this.enableDisableRemoveBlockButton();
        }
    },


    /**
     * @private
     */
    initLayout: function () {
        var me = this;

        // Edit form mode
        if (me.content) {
            me.addBlockAt(1, false);
        } else {
            me.addBlockAt(0, true);
        }

        me.add(me.createActionsContainer());
    },


    /**
     * @private
     */
    createAddBlockButton: function () {
        var me = this;

        return {
            xtype: 'button',
            text: 'Add ' + me.contentTypeItemConfig.label,
            handler: function (button) {
                me.addBlockAt((me.items.items.length - 1), true);
            }
        };
    },


    /**
     * @private
     */
    createActionsContainer: function () {
        var me = this;

        return {
            xtype: 'container',
            layout: {
                type: 'table',
                columns: 2,
                tableAttrs: {
                    style: 'width: 100%'
                }
            },
            items: [
                me.createAddBlockButton(),
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
                    /*
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
                     */
                }
            }
        };
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
            items: [me.createBlockHeader()],
            getValue: function () {
                var value = [];
                this.items.each(function (item) {
                    if (item.getValue) {
                        value = value.concat(item.getValue());
                    }
                });
                return value;
            }
        });

        // Rename argument createBlankBlock. hasContent
        if (createBlankBlock) {
            me.addComponentsBasedOnContentType(me.contentTypeItemConfig.items, block);
        } else {
            me.addComponentsBasedOnContentData(me.content.value, me.contentTypeItemConfig.items, block);
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
                    html: '<h6>' + (me.contentTypeItemConfig.label || '{No label}') + ': </h6>'
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
        };
    },


    /**
     * @private
     */
    setIndent: function () {
        var me = this;
        var parent = me.up();
        var parentIsBlock = parent.cls && parent.cls.indexOf('formitem-set-block') > -1;
        if (parentIsBlock) {
            me.margin = '10 0 10 20';
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
        new Admin.lib.Sortable(this,
            {
                proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
                handle: '.admin-drag-handle'
            });
    },

    getValue: function () {
        var value = [];
        var me = this;
        me.items.each(
            function (item, index) {
                if (item.getValue) {
                    var currentItemValue = item.getValue();
                    if (currentItemValue instanceof Array) {
                        Ext.each(currentItemValue, function (itemValue) {
                            itemValue.path = me.name.concat('[', index, ']', '.', itemValue.path);
                        });
                    } else {
                        currentItemValue.path = me.name.concat('[', index, ']', '.', currentItemValue);
                    }
                    value = value.concat(currentItemValue);
                }
            });
        return value;
    }

});