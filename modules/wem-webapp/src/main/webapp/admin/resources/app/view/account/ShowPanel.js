Ext.define('Admin.view.account.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accountShow',

    requires: [
        'Admin.view.account.BrowseToolbar',
        'Admin.view.account.GridPanel',
        'Admin.view.account.DetailPanel'
    ],

    layout: 'border',
    border: false,

    initComponent: function () {
        this.items = [
            {
                region: 'north',
                xtype: 'browseToolbar'
            },
            {
                region: 'center',
                xtype: 'accountGrid',
                flex: 2
            },
            {
                region: 'south',
                xtype: 'accountDetail',
                flex: 1
            }
        ];

        this.callParent(arguments);
    }

});
