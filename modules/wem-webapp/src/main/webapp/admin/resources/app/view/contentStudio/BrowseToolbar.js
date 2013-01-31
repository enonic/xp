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
                xtype: 'splitbutton',
                text: ' New',
                listeners: {
                    click: function (button) {
                        button.showMenu();
                    }
                },
                cls: 'x-btn-as-arrow',
                menu: [
                    {
                        text: 'Content Type',
                        action: 'newContentType'
                    },
                    {
                        text: 'Mixin',
                        action: 'newMixin'

                    },
                    {
                        text: 'Relationship type',
                        action: 'newRelationshipType'
                    }
                ]
            },
            {
                text: 'Edit',
                disabled: true,
                action: 'editBaseType'
            },
            {
                text: 'Delete',
                disabled: true,
                action: 'deleteBaseType'
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
