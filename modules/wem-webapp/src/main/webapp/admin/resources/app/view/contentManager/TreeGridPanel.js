Ext.define('Admin.view.contentManager.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTreeGridPanel',
    store: 'Admin.store.contentManager.ContentStore',
    treeStore: 'Admin.store.contentManager.ContentTreeStore',

    border: false,

    iconClasses: {
        "myModule:mySite": 'icon-site-32',
        "myModule:myType": 'icon-content-32',
        "News:Article": 'icon-content-32',
        "News:Article2": 'icon-content-32'
    },

    gridConf: {
        selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36}),
        plugins: [ 'persistentGridSelection' ]
    },

    treeConf: {
        selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
    },

    initComponent: function () {
        var me = this;
        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                scope: me,
                flex: 1
            },
            {
                text: 'Status',
                //dataIndex: 'type',
                renderer: this.statusRenderer
            },
            {
                text: 'Owner',
                dataIndex: 'owner',
                sortable: true
            },
            {
                text: 'Modified',
                dataIndex: 'modifiedTime',
                renderer: this.prettyDateRenderer,
                scope: me,
                sortable: true
            }
        ];
        this.callParent(arguments);
    },


    nameRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var account = record.data;
        var iconCls = '';
        var activeListType = this.getActiveList().itemId;
        return Ext.String.format(Templates.contentManager.treeGridPanelNameRenderer, activeListType, iconCls, value, account.path);
    },

    statusRenderer: function () {
        return "Online";
    },

    prettyDateRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        try {
            if (parent && Ext.isFunction(parent.humane_date)) {
                return parent.humane_date(value);
            } else {
                return value;
            }
        }
        catch (e) {
            return value;
        }
    }

});
