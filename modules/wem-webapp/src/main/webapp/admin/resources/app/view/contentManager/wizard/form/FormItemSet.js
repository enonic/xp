Ext.define('Admin.view.contentManager.wizard.form.FormItemSet', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.FormItemSet',

    requires: [
        'Admin.lib.Sortable'
    ],

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator',
        formItemOccurrencesHandler: 'Admin.view.contentManager.wizard.form.FormItemOccurrencesHandler'
    },

    formItemSetConfig: undefined,

    content: null, // Blocks

    isCollapsed: false,

    fieldLabel: '',

    margin: '0 0 10 0',
    cls: 'admin-sortable admin-formitemset-block',
    padding: '0 10 0 10',

    initComponent: function () {
        var min = this.formItemSetConfig.occurrences.minimum;
        var dataSet = !Ext.isEmpty(this.value) ? this.value[0].value : undefined;
        this.dockedItems = [this.createFormItemSetHeader(this.copyNo > min)];
        this.items = [];
        this.callParent(arguments);
        this.addComponentsBasedOnContentType(this.formItemSetConfig.items, this, dataSet);
    },

    listeners: {
        beforerender: function () {
            this.handleOccurrences(this.formItemSetConfig.occurrences.minimum);
        },
        render: function () {
            this.initSortable();
        }
    },

    /**
     * @private
     */
    createFormItemSetHeader: function (closable) {
        var me = this;
        var label = {
            xtype: 'component',
            cls: 'admin-drag-handle',
            html: '<h6>' + (me.formItemSetConfig.label || '{No label}') + ': </h6>'
        };
        var removeBtn = {
            tdAttrs: {
                align: 'right'
            },
            xtype: 'button',
            iconCls: 'icon-remove icon-2x icon-grey',
            itemId: 'remove-block-button',
            cls: 'nobg',
            padding: 0,
            scale: 'medium',
            handler: function (btn) {
                me.removeCopy();
            }
        };
        var items = closable ? [label, removeBtn] : [label];
        return {
            xtype: 'container',
            margin: '10 0 10 0',
            padding: '0 0 5 0',
            dock: 'top',
            cls: 'header',
            layout: {
                type: 'table',
                columns: 3,
                tableAttrs: {
                    style: 'width: 100%'
                }
            },
            items: items
        };
    },

    /**
     * @private
     */
    initSortable: function () {
        new Admin.lib.Sortable(this.up(),
            {
                proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
                group: this.name,
                name: this.formItemSetConfig.label,
                handle: '.admin-drag-handle'
            });
    },

    setCollapsed: function (collapsed) {
        this.items.each(function (item) {
            item.setVisible(collapsed);
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
            }
        );
        return value;
    },

    setValue: function () {

    },

    getConfig: function () {
        return this.formItemSetConfig;
    }

});