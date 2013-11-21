Ext.define('Admin.plugin.GridToolbarPlugin', {
    extend: 'Object',
    alias: 'plugin.gridToolbarPlugin',

    pluginId: 'gridToolbarPlugin',

    constructor: function (config) {
        if (config) {
            Ext.apply(this, config);
        }
    },

    init: function (toolbar) {
        var me = this;

        me.toolbar = toolbar;

        me.resultTextItem = <any> Ext.create('Ext.toolbar.TextItem', {
            text: '',
            hidden: !!me.toolbar.resultCountHidden
        });
        me.selectAllButton = me.createSelectAllButton();
        me.clearSelectionButton = me.createClearSelectionButton();
        me.tbFill = <any> Ext.create('Ext.toolbar.Fill');
        me.orderByButton = me.createOrderByButton();
        me.orderByDirectionButton = me.createOrderByDirectionButton();

        if (Ext.isFunction(me.toolbar.store.getCount)) {
            me.updateResultCount(me.getCount(me.toolbar.store));
        } else if (Ext.isString(me.toolbar.store)) {
            me.toolbar.store = Ext.StoreManager.lookup(me.toolbar.store);
        }

        me.toolbar.insert(0, me.resultTextItem);
        me.toolbar.insert(1, me.selectAllButton);
        me.toolbar.insert(2, Ext.create('Ext.toolbar.TextItem', {text: ' | '}));
        me.toolbar.insert(3, me.clearSelectionButton);
        console.log("param : ", this.disableSorting);
        if (!(me.toolbar.store instanceof Ext.data.TreeStore) && !this.disableSorting) {
            me.toolbar.insert(4, me.tbFill);
            me.toolbar.insert(5, me.orderByButton);
            me.toolbar.insert(6, me.orderByDirectionButton);
        }

        me.orderByButton.addListener('change', function () {
            me.doSort();
        });
        me.orderByDirectionButton.addListener('change', function () {
            me.doSort();
        });

        if (me.toolbar.store) {
            var loadEventName = me.toolbar.store.buffered ? 'prefetch' : 'load';
            me.toolbar.store.on(loadEventName, function (store) {
                me.updateResultCount(me.getCount(store));
            });

            // TODO: Listen for other store changes
        }

        if (me.toolbar.gridPanel) {
            me.toolbar.gridPanel.getSelectionModel().on('selectionchange', function (model, selected, eOpts) {
                me.updateSelectAll(selected);
                me.updateClearSelection(selected);
            });

            // TODO: Listen for other grid changes
        }
    },

    createSelectAllButton: function () {
        var me = this;
        return <any> Ext.create('Ext.Component', {
            autoEl: {
                tag: 'a',
                href: 'javascript:;',
                html: 'Select All',
                cls: 'admin-grid-toolbar-btn-none-selected'
            },
            listeners: {
                render: function (cmp) {
                    cmp.el.on('click', function () {
                        // don't update text here, it will be done on selectionchange
                        if (cmp.el.hasCls('admin-grid-toolbar-btn-none-selected')) {
                            me.toolbar.gridPanel.getSelectionModel().selectAll();
                        } else {
                            me.toolbar.gridPanel.getSelectionModel().deselectAll();
                        }
                    });
                }
            }
        });
    },

    createClearSelectionButton: function () {
        var me = this;
        return <any> Ext.create('Ext.Component', {
            autoEl: {
                tag: 'a',
                href: 'javascript:;',
                html: ' Clear Selection',
                cls: 'admin-grid-toolbar-btn-clear-selection'
            },
            listeners: {
                render: function (cmp) {
                    cmp.el.on('click', function () {
                        // don't update text here, it will be done on selectionchange
                        if (cmp.el.hasCls('admin-grid-toolbar-btn-clear-selection')) {
                            me.toolbar.gridPanel.getSelectionModel().deselectAll();
                        }
                    });
                }
            }
        });
    },

    createOrderByButton: function () {
        var me = this;
        var menuItems = me.createOrderByMenuItems();
        return new Ext.button.Cycle({
            showText: true,
            prependText: 'Order by ',
            menu: {
                items: menuItems
            }
        });
    },

    createOrderByDirectionButton: function () {
        return new Ext.button.Cycle({
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
        this.resultTextItem.setText(count + ' result(s) - ');
    },

    setResultCountVisible: function (visible) {
        this.resultTextItem.setVisible(visible);
    },

    updateSelectAll: function (selected) {
        var btn = this.selectAllButton;
        var isSelectMode = btn.el.hasCls('admin-grid-toolbar-btn-none-selected');
        var areAllRecordsSelected = !Ext.isEmpty(selected) && this.getCount(this.toolbar.store) == selected.length;
        // switch from select all to deselect all in case we selected all records, and vice versa otherwise
        if (areAllRecordsSelected && isSelectMode) {
            btn.update('Deselect all');
            btn.el.removeCls('admin-grid-toolbar-btn-none-selected');
        } else if (!areAllRecordsSelected && !isSelectMode) {
            btn.update('Select all');
            btn.el.addCls('admin-grid-toolbar-btn-none-selected');
        }
    },

    updateClearSelection: function (selected) {
        var btn = this.clearSelectionButton;
        var count = selected.length;
        if (count > 0) {
            btn.update('Clear selection (' + selected.length + ')');
        } else {
            btn.update('Clear selection');
        }
    },

    getCount: function (store) {
        if (store instanceof Ext.data.Store) {
            return store.getTotalCount();
        } else if (store instanceof Ext.data.TreeStore) {
            // We always have virtual root node, no need to count it
            return this.countTreeNodes(store.getRootNode()) - 1;
        } else {
            return undefined;
        }
    },

    countTreeNodes: function (node) {
        if (this.toolbar.countTopLevelOnly) {
            return Ext.isEmpty(node.childNodes) ? 1 : 1 + node.childNodes.length;
        } else {
            var count = 1;
            if (!Ext.isEmpty(node.childNodes)) {
                node.eachChild(function (child) {
                    count += this.countTreeNodes(child);
                }, this);
            }
            return count;
        }
    }

});
