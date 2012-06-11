Ext.define( 'Admin.view.contentManager.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.contentGrid',

    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.SlidingPagerPlugin'
    ],
    plugins: [ 'persistentGridSelection' ],
    layout: 'fit',
    multiSelect: true,
    columnLines: true,
    frame: false,
    store: 'Admin.store.contentManager.ContentStore',

    initComponent: function()
    {
        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'name',
                sortable: true,
                renderer: this.nameRenderer,
                flex: 1
            },
            {
                text: 'Type',
                dataIndex: 'type',
                sortable: true
            },
            {
                text: 'Owner',
                dataIndex: 'owner',
                sortable: true
            },
            {
                text: 'Last Modified',
                dataIndex: 'lastModified',
                renderer: this.prettyDateRenderer,
                sortable: true
            }
        ];

        this.tbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            plugins: ['slidingPagerPlugin']
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true,
            loadMask: true
        };

        this.selModel = Ext.create( 'Ext.selection.CheckboxModel', {
            //checkOnly: true
        } );

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        var account = record.data;
        var photoUrl = this.resolvePhotoUrl(account);

        return Ext.String.format( Templates.contentManager.gridPanelNameRenderer, photoUrl, value, account.name, account.userStore );
    },

    resolvePhotoUrl: function( account )
    {
        var url;
        var isSite = account.type === 'site';
        if ( isSite )
        {
            url = 'resources/images/icons/32x32/earth2.png';
        }
        else
        {
            url = 'resources/images/icons/32x32/cubes_blue.png';
        }

        return url;
    },

    prettyDateRenderer: function( value, p, record )
    {
        try
        {
            if ( parent && Ext.isFunction( parent.humane_date ) )
            {
                return parent.humane_date( value );
            }
            else
            {
                return value;
            }
        }
        catch( e )
        {
            return value;
        }
    }
} );
