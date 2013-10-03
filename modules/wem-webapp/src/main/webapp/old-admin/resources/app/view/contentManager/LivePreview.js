Ext.define('Admin.view.contentManager.LivePreview', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLive', // Post 18/4 rename livePreview

    bodyStyle: {
        backgroundColor: '#ccc'
    },

    requires: [
        'Admin.view.contentManager.contextwindow.ContextWindow'
    ],

    // For 18/4. todo: create CSS classes for the elements.
    html: '<div style="display:table; height:100%; width: 100%;"><div style="display:table-row"><div style="display:table-cell; height:100%; vertical-align: middle; text-align:center;"><iframe style="border: 0 none; width: 100%; height: 100%; box-shadow: 0 0 10px rgba(0, 0, 0, 0.2)"></iframe></div></div></div>',

    autoScroll: true,
    styleHtmlContent: true,
    layout: 'fit',

    iFrameLoaded: false,

    contextWindow: undefined,

    initComponent: function () {
        var me = this;

        me.on('afterrender', function () {
            if (me.actionButton) {
                me.renderActionButton();
            }
        });

        this.callParent(arguments);
    },


    getIframe: function () {
        return this.getTargetEl().down('iframe');
    },


    renderActionButton: function () {
        var me = this;

        Ext.create('widget.container', {
            renderTo: me.getEl(),
            floating: true,
            shadow: false,
            padding: '5 20 0',
            // Can we find a more ExtJS way to align the button to the right?
            style:'width: 100%; text-align: right',
            border: 0,
            items: [
                {
                    xtype: 'tbfill'
                },
                Ext.apply(me.actionButton, {border: 0})
            ]
        });
    },


    load: function (url, isEdit) {
        var me = this;
        var iFrame = this.getIframe();
        isEdit = isEdit || false;

        if (isEdit) {
            iFrame.on('load', function () {
                me.contextWindow = new Admin.view.contentManager.contextwindow.ContextWindow({
                    renderTo: me.getEl(),
                    liveEditIFrameDom: iFrame.dom
                });
            });
        }

        if (!Ext.isEmpty(url)) {
            iFrame.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
            this.iFrameLoaded = true;
        } else {
            iFrame.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }

});
