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
                action: 'newContentType'
            },

            {
                text: 'Edit',
                disabled: true,
                action: 'editContentType'
            },
            {
                text: 'Delete',
                disabled: true,
                action: 'deleteContentType'
            },

            {
                text: 'Open',
                disabled: true,
                action: 'viewContentType'
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
