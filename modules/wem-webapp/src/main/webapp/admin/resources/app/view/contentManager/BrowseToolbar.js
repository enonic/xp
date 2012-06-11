Ext.define( 'Admin.view.contentManager.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',

    border: false,

    initComponent: function()
    {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        xtype: 'splitbutton',
                        text: ' New',
                        action: 'newContent',
                        iconCls: 'icon-content-add-24',
                        cls: 'x-btn-as-arrow',
                        menu: Ext.create( 'Admin.view.MegaMenu', {
                            recentCount: 4,
                            cookieKey: 'admin.contentmanager.megamenu',
                            url: 'resources/data/contentManagerMenu.json'
                        } )
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Publish',
                        iconCls: 'icon-publish-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Edit',
                        action: 'editContent',
                        iconCls: 'icon-edit-generic'
                    },
                    {
                        text: 'Delete',
                        action: 'deleteContent',
                        iconCls: 'icon-delete-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Duplicate',
                        action: 'duplicateContent',
                        iconCls: 'icon-copy-24'
                    },
                    {
                        text: 'Move',
                        iconCls: 'icon-move-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Relations',
                        iconCls: 'icon-relation-24'
                    },
                    {
                        text: 'History',
                        iconCls: 'icon-history-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'View',
                        action: 'viewContent',
                        iconCls: 'icon-view-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Export',
                        iconCls: 'icon-export-24'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );
