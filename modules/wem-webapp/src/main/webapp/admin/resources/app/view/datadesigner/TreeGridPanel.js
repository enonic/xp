Ext.define('Admin.view.datadesigner.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTypeTreeGridPanel',
    store: 'Admin.store.datadesigner.ContentTypeStore',
    treeStore: 'Admin.store.datadesigner.ContentTypeTreeStore',
    requires: 'Admin.view.datadesigner.BrowseToolbar',
    dockedItems: [
        {
            xtype: 'datadesigner.browseToolbar'
        }
    ],

    nodeIconClasses: {
        form: 'icon-form-32',
        folder: 'icon-folder-32',
        media: 'icon-media-32',
        shortcut: 'icon-shortcut-32',
        struct: 'icon-struct-32',
        shortcut: 'icon-shortcut-32'
    },

    initComponent: function () {
        this.columns = [
            {
                header: 'Name',
                dataIndex: 'name',
                flex: 1,
                renderer: this.nameRenderer
            },
            {
                header: 'Last Modified',
                dataIndex: 'lastModified',
                renderer: this.prettyDateRenderer
            }
        ];
        this.callParent(arguments);
    },

    nameRenderer: function (value, p, record) {
        var contentType = record.data;
        var icon = contentType.icon === '' ? 'resources/images/icons/32x32/cubes.png' : contentType.icon;
        return Ext.String.format(Templates.datadesigner.gridPanelRenderer, icon, contentType.displayName, contentType['extends']);
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
