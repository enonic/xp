Ext.define('Admin.view.IframeContainer', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.iframe',

    url: undefined,
    html: '<iframe style="border: 0 none; width: 100%; height: 100%;"></iframe>',
    autoScroll: false,
    styleHtmlContent: true,
    iFrameCls: undefined,

    listeners: {
        afterrender: function (panel) {
            if (this.url) {
                this.load(this.url);
            }
        }
    },


    initComponent: function () {

        this.callParent(arguments);

    },

    load: function (url, isEdit) {
        var iframe = this.getEl().down('iframe');

        isEdit = isEdit || false;
        if (!Ext.isEmpty(url)) {
            iframe.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
            /*            if (this.iFrameCls) {
             console.log(iframe.dom.contentDocument);
             iframe.dom.contentDocument.body.className = "test";
             }*/
        } else {
            iframe.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }

});
