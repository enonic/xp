Ext.define('Admin.view.TreeGridPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.treeGridPanel',

    layout: 'card',

    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.GridToolbarPlugin'
    ],

    treeConf: {},

    gridConf: {},

    keyField: 'key',

    initComponent: function () {
        var me = this;

        var treeColumns = Ext.clone(this.columns);
        if (Ext.isEmpty(treeColumns)) {
            throw "this.columns can't be null";
        }

        treeColumns[0].xtype = 'treecolumn';
        // We don't need renderer for tree column
        //delete treeColumns[0].renderer;

        var treePanel = {
            xtype: 'treepanel',
            cls: 'admin-tree',
            hideHeaders: true,
            itemId: 'tree',
            useArrows: true,
            border: false,
            rootVisible: false,

            viewConfig: {
                trackOver: true,
                stripeRows: true,
                loadMask: {
                    store: me.treeStore
                }
            },
            store: this.treeStore,
            columns: treeColumns
        };
        treePanel = Ext.apply(treePanel, me.treeConf);

        var gridPanel = {
            xtype: 'grid',
            itemId: 'grid',
            cls: 'admin-grid',
            border: false,
            hideHeaders: true,
            plugins: [],
            viewConfig: {
                trackOver: true,
                stripeRows: true,
                loadMask: {
                    store: me.store
                }
            },
            store: this.store,
            columns: this.columns
        };
        gridPanel = Ext.apply(gridPanel, me.gridConf);

        this.items = [treePanel, gridPanel];
        this.callParent(arguments);
        var treeStore = this.down('#tree').store;
        me.mon(treeStore, 'beforeappend', function (parentNode, node, opts) {
            var iconCls = me.resolveIconClass(node);
            if (iconCls) {
                node.set('iconCls', iconCls);
            }
        });

        var grid = this.down('#grid');
        grid.addDocked({
            xtype: 'toolbar',
            cls: 'admin-white-toolbar',
            dock: 'top',
            store: this.store,
            gridPanel: grid,
            plugins: ['gridToolbarPlugin']
        });

        var tree = this.down('#tree');
        tree.addDocked({
            xtype: 'toolbar',
            cls: 'admin-white-toolbar',
            dock: 'top',
            store: this.treeStore,
            gridPanel: tree,
            plugins: ['gridToolbarPlugin']
        });
    },

    resolveIconClass: function (node) {
        var me = this;
        var iconCls = node.get('iconCls');
        var nodeType = node.get('type');
        if (Ext.isEmpty(iconCls) && me.iconClasses && me.iconClasses[nodeType]) {
            iconCls += " " + me.iconClasses[nodeType];
        }
        console.log(iconCls);
        return iconCls;
    },

    // possible values : 0,1,tree,grid
    setActiveList: function (listId) {
        this.getLayout().setActiveItem(listId);
    },

    getActiveList: function () {
        return this.getLayout().getActiveItem();
    },

    getSelection: function () {
        var selection = [];
        var activeList = this.getActiveList();
        if (activeList.xtype === 'treepanel') {
            selection = activeList.getSelectionModel().getSelection();
        } else if (activeList.xtype === 'grid') {
            var plugin = activeList.getPlugin('persistentGridSelection');
            if (plugin) {
                selection = plugin.getSelection();
            } else {
                selection = activeList.getSelectionModel().getSelection();
            }
        }
        return selection;
    },

    select: function (key, keepExisting) {

        var activeList = this.getActiveList();
        var selModel = activeList.getSelectionModel();

        if (activeList.xtype === 'treepanel') {
            var node = activeList.getRootNode().findChild(this.keyField, key);
            if (node) {
                selModel.select(node, keepExisting);
            }
        } else if (activeList.xtype === 'grid') {
            var record = activeList.getStore().findRecord(this.keyField, key);
            if (record) {
                selModel.select(record, keepExisting);
            }
        }
    },

    // -1 deselects all
    deselect: function (key) {

        var activeList = this.getActiveList();
        var selModel = activeList.getSelectionModel();

        if (key === -1) {
            if (activeList.xtype === 'treepanel') {
                selModel.deselectAll();
            } else if (activeList.xtype === 'grid') {
                var plugin = activeList.getPlugin('persistentGridSelection');
                if (plugin) {
                    plugin.clearSelection();
                } else {
                    selModel.deselectAll();
                }
            }
        } else {
            if (activeList.xtype === 'treepanel') {
                var selNodes = selModel.getSelection();
                var i;
                for (i = 0; i < selNodes.length; i++) {
                    var selNode = selNodes[i];
                    if (key === selNode.get(this.keyField)) {
                        selModel.deselect(selNode);
                    }
                }
            } else if (activeList.xtype === 'grid') {
                var record = activeList.getStore().findRecord(this.keyField, key);
                if (record) {
                    selModel.deselect(record);
                }
            }
        }
    },

    refresh: function () {
        var activeList = this.getActiveList();
        var currentStore = activeList.store;
        if (activeList.xtype === 'treepanel') {
            currentStore.load();
        } else if (activeList.xtype === 'grid') {
            currentStore.loadPage(currentStore.currentPage);
        }
    }
});
