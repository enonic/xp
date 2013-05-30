Ext.define('Admin.view.contentManager.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentShow',

    /*    requires: [
     'Admin.view.contentManager.BrowseToolbar',
     'Admin.view.contentManager.TreeGridPanel',
     'Admin.view.contentManager.DetailPanel'
     ],*/

    layout: 'border',
    border: false,

    initComponent: function () {

        /* For 18/4 demo */
        var contentIsOpenedFromPortal = document.location.href.indexOf('/open') > -1;

        this.items = [
            {
                region: 'north',
                xtype: 'browseToolbar'
            },
            {
                xtype: 'contentTreeGridPanel',
                region: 'center',
                itemId: 'contentList',
                flex: 1
            },
            {
                region: 'south',
                split: true,
                collapsible: true,
                header: false,
                xtype: 'contentDetail',
                isLiveMode: contentIsOpenedFromPortal,

                showToolbar: false,
                flex: 1
            },
            {
                region: 'east',
                split: true,
                collapsible: true,
                header: false,
                xtype: 'contentDetail',
                isLiveMode: contentIsOpenedFromPortal,

                showToolbar: false,
                flex: 1,
                hidden: true,
                isVertical: true
            }
        ];

        this.callParent(arguments);
    }

});
