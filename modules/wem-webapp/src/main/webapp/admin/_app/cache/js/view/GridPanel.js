Ext.define( 'App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.systemCacheGrid',

    requires: [
        'Admin.plugin.PageSizePlugin'
    ],
    title: 'System Caches',
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'SystemCacheStore',
    features: [{ftype:'grouping'}],

    initComponent: function()
    {
        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex:1
            },
            {
                text: 'Count',
                dataIndex: 'objectCount',
                xtype: 'numbercolumn',
                format:'0,000',
                sortable: true
            },
            {
                text: 'Size',
                dataIndex: 'memoryCapacity',
                xtype: 'numbercolumn',
                format:'0,000',
                sortable: true
            },
            {
                text: 'Hits',
                dataIndex: 'cacheHits',
                xtype: 'numbercolumn',
                sortable: true
            },
            {
                text: 'Misses',
                dataIndex: 'cacheMisses',
                xtype: 'numbercolumn',
                sortable: true
            },
            {
                text: 'Time To Live',
                dataIndex: 'timeToLive',
                xtype: 'numbercolumn',
                sortable: true
            }
        ];

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    }
});
