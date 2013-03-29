Ext.define('Admin.view.contentManager.LivePreview', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLive',

    html: '<iframe style="border: 0 none; width: 100%; height: 100%;"></iframe>',
    autoScroll: false,
    styleHtmlContent: true,
    layout: 'fit',

    actionButton: undefined,

    initComponent: function () {
        this.dockedItems = [
            {
                xtype: 'panel',
                layout: 'hbox',
                border: 0,
                dock: 'top',
                items: [
                    {
                        xtype: 'tbfill'
                    },
                    Ext.apply(this.actionButton, {border: 0})
                ]
            }
        ];
        this.callParent(arguments);

    },

    load: function (url, isEdit) {
        var iframe = this.getTargetEl().down('iframe');
        isEdit = isEdit || false;
        if (!Ext.isEmpty(url)) {
            iframe.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
        } else {
            iframe.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }

});
