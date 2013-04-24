Ext.define('Admin.view.TreeGridPanel', {
    extend: 'Admin.view.BaseTreeGridPanel',
    alias: 'widget.spaceTreeGrid',

    store: 'Admin.store.SpaceStore',
    treeStore: 'Admin.store.SpaceTreeStore',

    border: false,

    keyField: 'name',

    activeItem: 'grid',

    gridConf: {
        selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
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
        var space = record.data;
        var activeListType = this.getActiveList().itemId;
        return Ext.String.format(this.nameTemplate, activeListType, space.iconUrl, value, space.name);
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
