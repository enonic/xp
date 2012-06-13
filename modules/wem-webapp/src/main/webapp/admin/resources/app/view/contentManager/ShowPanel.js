Ext.define( 'Admin.view.contentManager.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentShow',

    requires: [
        'Admin.view.contentManager.BrowseToolbar',
        'Admin.view.contentManager.TreePanel',
        'Admin.view.contentManager.GridPanel',
        'Admin.view.contentManager.DetailPanel'
    ],

    layout: 'border',
    border: false,
    padding: 5,

    initComponent: function()
    {

        this.tbar = {
            xtype: 'browseToolbar'
        };

        this.items = [
            {
                layout: 'card',
                region: 'center',
                itemId: 'contentList',
                flex: 2,
                items: [
                    {
                        itemId: 'tree',
                        xtype: 'contentTree'
                    },
                    {
                        itemId: 'grid',
                        xtype: 'contentGrid'
                    }
                ]
            },
            {
                region: 'south',
                split: true,
                xtype: 'contentDetail',
                showToolbar: false,
                flex: 1
            }
        ];

        this.callParent( arguments );
    },

    // possible values : 0,1,tree,grid
    setActiveList: function( listId )
    {
        this.down( '#contentList' ).getLayout().setActiveItem( listId );
    },

    getActiveList: function()
    {
        return this.down( '#contentList' ).getLayout().getActiveItem();
    },

    getSelection: function()
    {
        var selection = [];
        var activeList = this.getActiveList();
        if ( activeList.xtype == 'contentTree' ) {
            selection = activeList.getSelectionModel().getSelection();
        } else if ( activeList.xtype == 'contentGrid' ) {
            var plugin = activeList.getPlugin( 'persistentGridSelection' );
            if ( plugin ) {
                selection = plugin.getSelection();
            } else {
                selection = activeList.getSelectionModel().getSelection();
            }
        }
        return selection;
    },

    // -1 deselects all
    deselect: function( key )
    {
        var activeList = this.getActiveList();
        var selModel = activeList.getSelectionModel();
        if ( key == -1 ) {
            if ( activeList.xtype == 'contentTree' ) {
                selModel.deselectAll();
            } else if ( activeList.xtype == 'contentGrid' ) {
                var plugin = activeList.getPlugin( 'persistentGridSelection' );
                if ( plugin ) {
                    plugin.clearSelection();
                } else {
                    selModel.deselectAll();
                }
            }
        } else {
            if ( activeList.xtype == 'contentTree' ) {
                var selNodes = selModel.getSelection();
                for ( var i = 0; i < selNodes.length; i++ ) {
                    var selNode = selNodes[i];
                    if ( key == selNode.get( 'key' ) ) {
                        selModel.deselect( selNode );
                    }
                }
            } else if ( activeList.xtype == 'contentGrid' ) {
                var record = activeList.getStore().findRecord( 'key', key );
                if ( record ) {
                    selModel.deselect( record );
                }
            }
        }
    }

} );
