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
    iconClasses: {
        "system:content": 'icon-icomoon-content-32',
        "system:folder": 'icon-icomoon-folder-32',
        "system:file": 'icon-icomoon-file-32',
        "system:page": 'icon-icomoon-page-32',
        "system:space": 'icon-icomoon-space-32',
        "system:shortcut": 'icon-icomoon-shortcut-32',
        "system:structured": 'icon-icomoon-structured-32',
        "system:unstructured": 'icon-icomoon-unstructured-32'
    },

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
        return Ext.String.format(Templates.contentStudio.treeGridPanelNameRenderer, activeListType, this.resolveIconClass(record),
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
