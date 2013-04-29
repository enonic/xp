Ext.define('Admin.view.BaseTreeGridPanel', {
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

    nameTemplate: '<div class="admin-{0}-thumbnail">' +
                  '<img src="{1}"/>' +
                  '</div>' +
                  '<div class="admin-{0}-description">' +
                  '<h6>{2}</h6>' +
                  '<p>{3}</p>' +
                  '</div>',

    initComponent: function () {
        var me = this;

        var gridSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
            keyField: me.keyField
        });

        var gridPanel = {
            xtype: 'grid',
            itemId: 'grid',
            cls: 'admin-grid',
            border: false,
            hideHeaders: true,
            viewConfig: {
                trackOver: true,
                stripeRows: true,
                loadMask: {
                    store: me.store
                }
            },
            store: this.store,
            columns: this.columns,
            plugins: [gridSelectionPlugin]
        };
        gridPanel = Ext.apply(gridPanel, me.gridConf);

        this.items = [gridPanel];
        this.callParent(arguments);

        var grid = this.down('#grid');
        grid.addDocked({
            xtype: 'toolbar',
            itemId: 'selectionToolbar',
            cls: 'admin-white-toolbar',
            dock: 'top',
            store: this.store,
            gridPanel: grid,
            resultCountHidden: true,
            plugins: ['gridToolbarPlugin']
        });
        grid.getStore().on('datachanged', this.fireUpdateEvent, this);

        this.addEvents('datachanged');
    },

    fireUpdateEvent: function (values) {
        this.fireEvent('datachanged', values);
    },

    // possible values : 0,1,tree,grid
    setActiveList: function (listId) {
        this.getLayout().setActiveItem(listId);
    },

    getActiveList: function () {
        return this.getLayout().getActiveItem();
    },

    getSelection: function () {
        var selection = [],
            activeList = this.getActiveList(),
            plugin = activeList.getPlugin('persistentGridSelection');

        if (plugin) {
            selection = plugin.getSelection();
        } else {
            selection = activeList.getSelectionModel().getSelection();
        }

        return selection;
    },

    select: function (key, keepExisting) {
        var activeList = this.getActiveList();
        var selModel = activeList.getSelectionModel();
        var keys = [].concat(key);
        var i;

        if (activeList.xtype === 'grid') {
            var store = activeList.getStore(),
                record;
            for (i = 0; i < keys.length; i++) {
                record = store.findRecord(this.keyField, keys[i]);
                if (record) {
                    selModel.select(record, keepExisting);
                }
            }
        }
    },

    // -1 deselects all
    deselect: function (key) {

        var activeList = this.getActiveList(),
            plugin = activeList.getPlugin('persistentGridSelection'),
            selModel = plugin ? plugin : activeList.getSelectionModel();

        if (!key || key === -1) {

            if (plugin) {
                plugin.clearSelection();
            } else {
                selModel.deselectAll();
            }
        } else {
            if (activeList.xtype === 'grid') {
                var record = activeList.getStore().findRecord(this.keyField, key);
                if (record) {
                    selModel.deselect(record);
                }
            }
        }
    },

    setRemoteSearchParams: function (params) {
        var activeList = this.getActiveList();
        var currentStore = activeList.store;
        currentStore.getProxy().extraParams = params;
    },

    setResultCountVisible: function (visible) {
        this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin').setResultCountVisible(visible);
    },

    updateResultCount: function (count) {
        this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin').updateResultCount(count);
    },

    removeAll: function () {
        var activeList = this.getActiveList();

        activeList.removeAll();
    },

    refresh: function () {

        var activeList = this.getActiveList();
        var currentStore = activeList.store;

        if (!currentStore.loading) {

            if (activeList.xtype === 'grid') {
                currentStore.loadPage(currentStore.currentPage);
            }
        }
    }
});
