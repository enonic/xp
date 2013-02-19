Ext.define('Admin.view.account.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.accountGrid',

    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.GridToolbarPlugin'
    ],
    plugins: ['persistentGridSelection'],

    cls: 'admin-grid',
    layout: 'fit',
    multiSelect: true,
    columnLines: true,
    hideHeaders: true,
    border: false,

    verticalScroller: {
        trailingBufferZone: 100,
        leadingBufferZone: 100,
        numFromEdge: 10,
        scrollToLoadBuffer: 0
    },

    invalidateScrollerOnRefresh: false,
    store: 'Admin.store.account.AccountStore',

    initComponent: function () {
        var me = this;
        me.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                flex: 1
            },
            {
                text: 'Username',
                dataIndex: 'name',
                hidden: true,
                sortable: true
            },
            {
                text: 'Userstore',
                dataIndex: 'userStore',
                hidden: true,
                sortable: true
            },
            {
                text: 'E-Mail',
                dataIndex: 'email',
                hidden: true,
                sortable: true
            },
            {
                text: 'Country',
                dataIndex: 'country',
                hidden: true,
                sortable: true
            },
            {
                text: 'Locale',
                dataIndex: 'locale',
                hidden: true,
                sortable: true
            },
            {
                text: 'Timezone',
                dataIndex: 'timezone',
                hidden: true,
                sortable: true
            },
            {
                text: 'Last Modified',
                dataIndex: 'modifiedTime',
                renderer: this.prettyDateRenderer,
                sortable: true
            }
        ];

        me.tbar = {
            xtype: 'toolbar',
            cls: 'admin-white-toolbar',
            store: me.store,
            gridPanel: me,
            plugins: ['gridToolbarPlugin']
        };

        me.viewConfig = {
            trackOver: false,
            stripeRows: true,
            loadMask: {
                store: me.store
            }
        };

        me.selModel = Ext.create('Ext.selection.CheckboxModel', {
            headerWidth: 36
        });

        me.callParent(arguments);

        if (me.verticalScroller) {
            Ext.Function.interceptBefore(this.verticalScroller, 'attemptLoad', me.saveSelection, me);
            me.store.on('datachanged', me.restoreSelection, me);
        }
    },

    nameRenderer: function (value, p, record) {
        var account = record.data;
        var photoUrl = account.image_url;

        return Ext.String.format(Templates.account.gridPanelNameRenderer, photoUrl, value, account.name, account.userStore);
    },

    prettyDateRenderer: function (value, p, record) {
        try {
            if (parent && Ext.isFunction(parent.humane_date)) {
                return parent.humane_date(value);
            } else {
                return value;
            }
        } catch (e) {
            return value;
        }
    },

    saveSelection: function () {
        if (!this.selectionCache) {
            var plugin = this.getPlugin('persistentGridSelection');
            var source = plugin ? plugin : this.getSelectionModel();
            this.selectionCache = source.getSelection();
        }
    },

    restoreSelection: function () {
        if (this.selectionCache) {
            this.getSelectionModel().select(this.selectionCache, true, false);
            delete this.selectionCache;
        }
    }
});
