Ext.define('Admin.view.contentManager.wizard.form.FormItemSet', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.FormItemSet',

    requires: [
        'Admin.lib.Sortable'
    ],

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator',
        fieldOccurrencesHandler: 'Admin.view.contentManager.wizard.form.FieldOccurrencesHandler'
    },

    contentTypeItemConfig: undefined,

    content: null, // Blocks

    isCollapsed: false,

    fieldLabel: '',

    margin: '10 0 10 0',

    cls: 'form-item-set',

    initComponent: function () {
        this.callParent(arguments);
        this.initLayout();
    },

    listeners: {
        beforerender: function () {
            this.handleOccurrences();
            this.setIndent();
        },
        render: function () {
            this.initSortable();
        }
    },


    /**
     * @private
     */
    initLayout: function () {

        // Edit form mode
        if (this.value) {
            this.addBlock(false);
        } else {
            this.addBlock(true);
        }

        this.add(this.createActionsContainer());
    },


    bindOccurrencesEventsHandlers: function () {
        this.on('copyadded', this.updateButtonState, this);
        this.on('copyremoved', this.updateButtonState, this);
    },

    updateButtonState: function () {
        var max = this.contentTypeItemConfig.occurrences.maximum;
        if (this.addButton) {
            if (this.nextField || this.copyNo === max) {
                this.addButton.hide();
            } else {
                this.addButton.show();
            }
        }

        var totalCount = 1;
        var tmp = this;
        // Find the very first copy
        while (tmp.prevField) {
            tmp = tmp.prevField;
        }
        var root = tmp;
        while (tmp.nextField) {
            tmp = tmp.nextField;
            totalCount++;
        }
        root.updateButtonStateInternal(totalCount);
        this.doLayout();
    },

    /**
     * @private
     */
    createAddBlockButton: function () {
        var me = this;

        this.addButton = Ext.create({
            xclass: 'widget.button',
            itemId: 'add-button',
            style: {
                float: 'left'
            },
            text: 'Add ' + me.contentTypeItemConfig.label,
            handler: function () {
                me.addCopy();
            }
        });
        return this.addButton;
    },


    /**
     * @private
     */
    createActionsContainer: function () {
        var me = this;

        return {
            xtype: 'container',
            itemId: 'actions-container',
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
    },


    /**
     * @private
     */
    addBlock: function (hasContent) {
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
        if (hasContent) {
            me.addComponentsBasedOnContentType(me.contentTypeItemConfig.items, block);
        } else {
            me.addComponentsBasedOnContentData(me.value[0].value, me.contentTypeItemConfig.items, block);
        }
        me.add(block);
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
                    html: '<span class="admin-drag-handle icon-align-justify icon-2x" style="display: inline-block"></span>'
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
                    iconCls: 'icon-remove icon-2x icon-grey',
                    itemId: 'remove-block-button',
                    cls: 'nobg',
                    scale: 'medium',
                    handler: function (btn) {
                        me.removeCopy();
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
    setDisableRemoveBlockButton: function (disable) {
        var me = this,
            button = Ext.ComponentQuery.query('#remove-block-button', me)[0];

        if (button) {
            button.setDisabled(disable);
        }
    },

    /**
     * @private
     * @param totalCount
     */
    updateButtonStateInternal: function (totalCount) {
        var min = this.contentTypeItemConfig.occurrences.minimum;
        var max = this.contentTypeItemConfig.occurrences.maximum;
        this.setDisableRemoveBlockButton(totalCount === min);
        if (this.nextField) {
            this.nextField.updateButtonStateInternal(totalCount);
        }
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
                            itemValue.path = me.name.concat('[', me.copyNo - 1, ']', '.', itemValue.path);
                        });
                    } else {
                        currentItemValue.path = me.name.concat('[', me.copyNo - 1, ']', '.', currentItemValue);
                    }
                    value = value.concat(currentItemValue);
                }
            });
        return value;
    },

    setValue: function () {

    }

});