Ext.define('Admin.view.account.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.accountGrid',

    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.SlidingPagerPlugin'
    ],
    plugins: ['persistentGridSelection'],
    layout: 'fit',
    multiSelect: true,
    columnLines: true,
    frame: false,
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
                dataIndex: 'lastModified',
                renderer: this.prettyDateRenderer,
                sortable: true
            }
        ];

        me.tbar = {
            xtype: 'pagingtoolbar',
            store: me.store,
            plugins: ['slidingPagerPlugin']
        };

        me.viewConfig = {
            trackOver: true,
            stripeRows: true,
            loadMask: {
                store: me.store
            }
        };

        me.selModel = Ext.create('Ext.selection.CheckboxModel', {
            //checkOnly: true
        });

        me.callParent(arguments);
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
    }
});
