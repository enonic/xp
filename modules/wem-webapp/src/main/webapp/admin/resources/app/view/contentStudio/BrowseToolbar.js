Ext.define('Admin.view.contentStudio.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentStudio.browseToolbar',

    cls: 'admin-toolbar',

    defaults: {
        scale: 'medium',
        iconAlign: 'top'
    },

    initComponent: function () {
        this.items = [

            {
                text: 'New',
                action: 'newBaseType'
            },
            {
                text: 'Edit',
                disabled: true,
                action: 'editBaseType'
            },
            {
                text: 'Open',
                disabled: true,
                action: 'viewContentType'
            },
            {
                text: 'Delete',
                disabled: true,
                action: 'deleteBaseType'
            },
            {
                text: 'Re-index',
                action: 'reindexContentTypes'
            },

            {
                text: 'Export',
                action: 'exportContentTypes'
            }
        ];

        this.callParent(arguments);
    }
});
