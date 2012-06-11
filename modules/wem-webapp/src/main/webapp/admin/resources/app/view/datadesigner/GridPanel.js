Ext.define( 'Admin.view.datadesigner.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.contentTypeGridPanel',
    store: 'Admin.store.datadesigner.ContentTypeStore',
    requires: 'Admin.view.datadesigner.BrowseToolbar',

    dockedItems: [
        {
            xtype: 'datadesigner.browseToolbar'
        }
    ],

    initComponent: function()
    {
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

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        var contentType = record.data;
        var icon = contentType.icon === '' ? 'resources/images/icons/32x32/cubes.png' : contentType.icon;
        return Ext.String.format( Templates.datadesigner.gridPanelRenderer, icon, contentType.displayName, contentType.extends );
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

});
