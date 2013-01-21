Ext.define('Admin.view.contentStudio.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTypeTreeGridPanel',
    store: 'Admin.store.contentStudio.BaseTypeStore',
    treeStore: 'Admin.store.contentStudio.BaseTypeTreeStore',
    requires: 'Admin.view.contentStudio.BrowseToolbar',
    dockedItems: [
        {
            xtype: 'contentStudio.browseToolbar'
        }
    ],

    initComponent: function () {
        this.columns = [
            {
                header: 'Name',
                dataIndex: 'displayName',
                flex: 1,
                renderer: this.nameRenderer,
                scope: this
            },
            {
                header: 'Module',
                dataIndex: 'module'
            },
            {
                header: 'Type',
                dataIndex: 'type'
            },
            {
                header: 'Modified',
                dataIndex: 'modifiedTime',
                renderer: this.prettyDateRenderer
            }
        ];
        this.callParent(arguments);
    },

    nameRenderer: function (value, p, record) {
        var baseType = record.data;
        var activeListType = this.getActiveList().itemId;
        var baseTypeIconUrl = baseType.iconUrl;
        return Ext.String.format(Templates.contentStudio.treeGridPanelNameRenderer, activeListType, baseTypeIconUrl, baseType.displayName,
            baseType.qualifiedName);
    },

    prettyDateRenderer: function (value, p, record) {
        try {
            if (parent && Ext.isFunction(parent.humane_date)) {
                return parent.humane_date(value);
            }
            else {
                return value;
            }
        }
        catch (e) {
            return value;
        }
    }
});
