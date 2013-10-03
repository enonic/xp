Ext.define('Admin.view.schemaManager.TreeGridPanel', {
    extend: 'Admin.view.TreeGridPanel',
    alias: 'widget.contentTypeTreeGridPanel',
    store: 'Admin.store.schemaManager.SchemaStore',
    treeStore: 'Admin.store.schemaManager.SchemaTreeStore',
    requires: 'Admin.view.schemaManager.BrowseToolbar',
    dockedItems: [
        {
            xtype: 'schemaManager.browseToolbar'
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
        var me = this;
        var schema = record.data;
        var activeListType = this.getActiveList().itemId;
        var schemaIconUrl = schema.iconUrl;
        return Ext.String.format(me.getNameColumnRendererTemplate(), activeListType, schemaIconUrl, schema.displayName,
            schema.qualifiedName);
    },

    prettyDateRenderer: function (value, p, record) {
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
    },

    getNameColumnRendererTemplate: function () {
        return '<div class="admin-{0}-thumbnail">' +
               '  <img src="{1}?size=32"/>' +
               '</div>' +
               '<div class="admin-{0}-description">' +
               '   <h6>{2}</h6>' +
               '   <p>{3}</p>' +
               '</div>';
    }
});
