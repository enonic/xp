Ext.define('Admin.view.contentManager.LivePreview', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLive',

    html: '<iframe style="border: 0 none; width: 100%; height: 100%;"></iframe>',
    autoScroll: false,
    styleHtmlContent: true,
    layout: 'fit',

    iFrameLoaded: false,

    actionButton: undefined,

    initComponent: function () {
        var me = this;
        this.dockedItems = [
            me.getActionButtonContainer()
        ];


        this.callParent(arguments);
    },

    getActionButtonContainer: function () {
        if (this.actionButton) {
            return {
                xtype: 'container',
                layout: 'hbox',
                border: 0,
                padding: '5 20 0',
                dock: 'top',
                items: [
                    {
                        xtype: 'tbfill'
                    },
                    Ext.apply(this.actionButton, {border: 0})
                ]
            };
        }
        return {};
    },

    load: function (url, isEdit) {
        var iframe = this.getTargetEl().down('iframe');
        isEdit = isEdit || false;
        if (!Ext.isEmpty(url)) {
            iframe.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
            this.iFrameLoaded = true;
        } else {
            iframe.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }

});
