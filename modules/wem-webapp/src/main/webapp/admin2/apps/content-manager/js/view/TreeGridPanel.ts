Ext.define('Admin.view.contentManager.TreeGridPanel', {
    extend: 'Admin.view.BaseTreeGridPanel',
    alias: 'widget.contentTreeGridPanel',
    store: 'Admin.store.contentManager.ContentStore',
    treeStore: 'Admin.store.contentManager.ContentTreeStore',

    border: false,

    keyField: 'path',

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
        var activeListType = this.getActiveList().itemId;

        var Templates_contentManager_treeGridPanelNameRenderer =
            '<div class="admin-{0}-thumbnail">' +
            '<img src="{1}"/>' +
            '</div>' +
            '<div class="admin-{0}-description">' +
            '<h6>{2}</h6>' +
            '<p>{3}</p>' +
            '</div>';

        return Ext.String.format(Templates_contentManager_treeGridPanelNameRenderer, activeListType, account.iconUrl, value, account.path);
    },

    statusRenderer: function () {
        return "Online";
    },

    prettyDateRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        try {
            //TODO: Fix to work with typescript
            /*if (parent && Ext.isFunction(parent.humane_date)) {
             return parent.humane_date(value);
             } else {
             return value;
             }*/
        }
        catch (e) {
            return value;
        }
    }

});
