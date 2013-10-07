Ext.define('App.view.Dashlet', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dashlet',

    cls: 'dashlet',
    title: 'Dashlet Panel',

    collapsible: true,
    closable: true,
    resizable: true,
    resizeHandles: 'n,s',
    autoScroll: true,
    border: false,
    frame: true,
    anchor: '100%',
    minHeight: 100,
    maxHeight: 500,
    autoHeight: false,
    bodyPadding: 10,
    layout: "fit",
    draggable: {
        moveOnDrag: false
    },

    html: 'Dashlet text',

    initComponent: function () {
        this.tools = this.getTools();
        this.callParent(arguments);
    },

    getTools: function () {
        return [
            {
                xtype: 'tool',
                type: 'gear',
                handler: function (e, target, panelHeader, tool) {
                    var dashlet = panelHeader.ownerCt;
                    dashlet.setLoading('Working...');
                    Ext.defer(function () {
                        dashlet.setLoading(false);
                    }, 2000);
                }
            }
        ];
    }
});