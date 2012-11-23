Ext.define('Admin.view.contentManager.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTreeGridPanel',
    store: 'Admin.store.contentManager.ContentStore',
    treeStore: 'Admin.store.contentManager.ContentTreeStore',

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

        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
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
                sortable: true
            }
        ];
        this.callParent(arguments);
    },


    nameRenderer: function (value, p, record) {
        var account = record.data;
        var iconCls = this.up('contentTreeGridPanel').resolveIconClass(record);

        return Ext.String.format(Templates.contentManager.gridPanelNameRenderer, iconCls, value, account.name, account.userStore);
    },

    statusRenderer: function () {
        return "Online";
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