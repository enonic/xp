Ext.define('App.view.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.systemCacheShow',

    requires: [
        'App.view.GridPanel',
        'App.view.DetailPanel'
    ],

    layout: 'border',
    border: false,

    initComponent: function () {
        this.items = [
            {
                region: 'center',
                xtype: 'systemCacheGrid',
                flex: 1
            },
            {
                region: 'south',
                xtype: 'systemCacheDetail',
                flex: 2
            }
        ];

        this.callParent(arguments);
    }

});
