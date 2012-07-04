Ext.define('Admin.view.contentManager.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentShow',

    requires: [
        'Admin.view.contentManager.BrowseToolbar',
        'Admin.view.contentManager.TreeGridPanel',
        'Admin.view.contentManager.DetailPanel'
    ],

    layout: 'border',
    border: false,
    padding: 5,

    initComponent: function () {

        this.tbar = {
            xtype: 'browseToolbar'
        };

        this.items = [
            {
                xtype: 'contentTreeGridPanel',
                region: 'center',
                itemId: 'contentList',
                flex: 2
            },
            {
                region: 'south',
                split: true,
                xtype: 'contentDetail',
                showToolbar: false,
                flex: 1
            }
        ];

        this.callParent(arguments);
    }

});
