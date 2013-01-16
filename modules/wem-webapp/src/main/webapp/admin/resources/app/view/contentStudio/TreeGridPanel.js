Ext.define('Admin.view.contentStudio.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTypeTreeGridPanel',
    store: 'Admin.store.contentStudio.ContentTypeStore',
    treeStore: 'Admin.store.contentStudio.ContentTypeTreeStore',
    requires: 'Admin.view.contentStudio.BrowseToolbar',
    dockedItems: [
        {
            xtype: 'contentStudio.browseToolbar'
        }
    ],

    typeField: 'qualifiedName',

    initComponent: function () {
        this.columns = [
            {
                header: 'Name',
                dataIndex: 'name',
                flex: 1,
                renderer: this.nameRenderer,
                scope: this
            },
            {
                header: 'Last Modified',
                dataIndex: 'modifiedTime',
                renderer: this.prettyDateRenderer
            }
        ];
        this.callParent(arguments);
    },

    nameRenderer: function (value, p, record) {
        var contentType = record.data;
        var activeListType = this.getActiveList().itemId;
        var contentTypeImgUrl = contentType.iconUrl;
        return Ext.String.format(Templates.contentStudio.treeGridPanelNameRenderer, activeListType, contentTypeImgUrl,
            contentType.name, contentType.qualifiedName);
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
