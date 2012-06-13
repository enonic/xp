Ext.define( 'Admin.view.contentManager.TreePanel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.contentTree',

    collapsible: true,
    useArrows: true,
    rootVisible: false,
    store: 'Admin.store.contentManager.ContentTreeStore',
    multiSelect: true,
    singleExpand: false,

    columns: [
        {
            text: 'Display Name',
            xtype: 'treecolumn', //this is so we know which column will show the tree
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
    ],

    nameRenderer: function( value, p, record )
    {
        var account = record.data;
        var photoUrl = this.resolvePhotoUrl( account );

        return Ext.String.format( Templates.contentManager.gridPanelNameRenderer, photoUrl, value, account.name,
                account.userStore );
    },

    resolvePhotoUrl: function( account )
    {
        var url;
        var isSite = account.type === 'site';
        if ( isSite ) {
            url = 'resources/images/icons/32x32/earth2.png';
        }
        else {
            url = 'resources/images/icons/32x32/cubes_blue.png';
        }

        return url;
    },

    prettyDateRenderer: function( value, p, record )
    {
        try {
            if ( parent && Ext.isFunction( parent.humane_date ) ) {
                return parent.humane_date( value );
            }
            else {
                return value;
            }
        }
        catch ( e ) {
            return value;
        }
    }


} );