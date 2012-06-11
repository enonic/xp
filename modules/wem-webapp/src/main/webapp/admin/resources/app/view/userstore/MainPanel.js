Ext.define('Admin.view.userstore.MainPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.mainPanel',

    layout: 'border',
    border: false,
    padding: 0,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                id: 'userstoreGridID',
                xtype: 'userstoreGrid',
                flex: 2
            },
            {
                region: 'south',
                split: true,
                xtype: 'userstorePreviewPanel',
                flex: 1
            },
            {
                region: 'north',
                xtype: 'browseToolbar'
            }
        ];

        this.callParent(arguments);
    }

});
