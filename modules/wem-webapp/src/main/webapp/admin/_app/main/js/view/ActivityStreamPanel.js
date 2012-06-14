Ext.define('App.view.ActivityStreamPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.activityStreamPanel',
    title: 'Activity Stream',
    tools:[
        {
            type:'gear'
        }
    ],
    collapsible: true,
    width: 270,
    minWidth: 200,
    maxWidth: 270,
    autoScroll: false,
    bodyCls: 'admin-activity-stream-panel-body',

    initComponent: function()
    {
        this.html = '<div id="admin-activity-stream-speak-out-panel-container"><!-- --></div>' +
                    '<div id="admin-activity-stream-messages-container"><!-- --></div>';

        this.callParent(arguments);
    }

});
