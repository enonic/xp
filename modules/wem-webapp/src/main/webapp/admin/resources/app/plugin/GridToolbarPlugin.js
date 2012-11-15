Ext.define('Admin.plugin.GridToolbarPlugin', {
    extend: 'Object',
    alias: 'plugin.gridToolbarPlugin',

    constructor: function (config) {
        if (config) {
            Ext.apply(this, config);
        }
    },

    init: function (toolbar) {
        var me = this;

        me.toolbar = toolbar;
        me.resultTextItem = Ext.create('Ext.toolbar.TextItem', {text: ''});
        me.selectAllButton = me.createSelectAllButton();
        me.tbFill = Ext.create('Ext.toolbar.Fill');
        me.orderByButton = me.createOrderByButton();
        me.orderByDirectionButton = me.createOrderByDirectionButton();

        me.toolbar.insert(0, me.resultTextItem);
        me.toolbar.insert(1, me.selectAllButton);
        me.toolbar.insert(2, me.tbFill);
        me.toolbar.insert(3, me.orderByButton);
        me.toolbar.insert(4, me.orderByDirectionButton);

        me.orderByButton.addListener('change', function () {
            me.doSort();
        });
        me.orderByDirectionButton.addListener('change', function () {
            me.doSort();
        });

        if (Ext.isFunction(me.toolbar.store.getCount)) {
            me.updateResultCount(me.toolbar.store.getCount());
        } else if (Ext.isString(me.toolbar.store)) {
            me.toolbar.store = Ext.StoreManager.lookup(me.toolbar.store);
        }

        if (me.toolbar.store) {
            me.toolbar.store.on('load', function (store) {
                me.updateResultCount(me.toolbar.store.getCount());
            });

            // TODO: Listen for other store changes
        }
    },

    createSelectAllButton: function () {
        var me = this;
        return Ext.create('Ext.Component', {
            autoEl: {
                tag: 'a',
                href: 'javascript:;',
                html: 'Select All',
                cls: 'admin-grid-toolbar-btn-none-selected'
            },
            listeners: {
                render: function (cmp) {
                    cmp.el.on('click', function () {
                        if (cmp.el.hasCls('admin-grid-toolbar-btn-none-selected')) {
                            me.toolbar.gridPanel.getSelectionModel().selectAll();
                            cmp.el.setHTML('Deselect all');
                        } else {
                            me.toolbar.gridPanel.getSelectionModel().deselectAll();
                            cmp.el.setHTML('Select all');
                        }
                        cmp.el.toggleCls('admin-grid-toolbar-btn-none-selected');
                    });
                }
            }
        });
    },

    createOrderByButton: function () {
        var me = this;
        var menuItems = me.createOrderByMenuItems();
        return Ext.create('Ext.button.Cycle', {
            showText: true,
            prependText: 'Order by ',
            menu: {
                items: menuItems
            }
        });
    },

    createOrderByDirectionButton: function () {
        return Ext.create('Ext.button.Cycle', {
            showText: true,
            prependText: 'Direction ',
            menu: {
                items: [
                    {text: 'ASC'},
                    {text: 'DESC'}
                ]
            }
        });
    },

    createOrderByMenuItems: function () {
        var me = this;
        var gridColumns = me.toolbar.gridPanel.columns;
        var menuItems = [];
        for (var i = 0; i < gridColumns.length; i++) {
            menuItems.push({
                text: gridColumns[i].text,
                dataIndex: gridColumns[i].dataIndex
            })
        }
        return menuItems;
    },

    doSort: function () {
        var me = this;
        var sortBy = me.orderByButton.getActiveItem().dataIndex;
        var direction = me.orderByDirectionButton.getActiveItem().text;

        me.toolbar.gridPanel.getStore().sort(sortBy, direction);
    },

    updateResultCount: function (count) {
        this.resultTextItem.setText(count + ' results - ');
    }

});
