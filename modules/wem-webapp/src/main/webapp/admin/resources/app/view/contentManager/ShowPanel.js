Ext.define( 'Admin.view.contentManager.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentShow',

    requires: [
        'Admin.view.contentManager.BrowseToolbar',
        'Admin.view.contentManager.GridPanel',
        'Admin.view.contentManager.DetailPanel'
    ],

    layout: 'border',
    border: false,
    padding: 5,

    initComponent: function()
    {
        var grid = Ext.create( 'Admin.view.contentManager.GridPanel', {
            region: 'center',
            flex: 2
        } );

        this.items = [
            {
                region: 'north',
                xtype: 'browseToolbar'
            },
            grid,
            {
                region: 'south',
                split: true,
                xtype: 'contentDetail',
                showToolbar: false,
                grid: grid,
                flex: 1
            }
        ];

        this.callParent( arguments );
    }

} );
