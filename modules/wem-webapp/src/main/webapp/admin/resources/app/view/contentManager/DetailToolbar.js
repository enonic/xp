Ext.define( 'Admin.view.contentManager.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentDetailToolbar',

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
            },
            '->',
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Live Mode',
                        action: 'toggleLive',
                        iconCls: 'icon-lightbulb-24',
                        enableToggle: true
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );
