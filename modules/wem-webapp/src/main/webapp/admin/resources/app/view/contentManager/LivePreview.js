Ext.define('Admin.view.contentManager.LivePreview', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLive', // Post 18/4 rename livePreview

    bodyStyle: {
        backgroundColor: '#212121'
    },

    html: '<div style="height: 100%; width: 100%;text-align:center"><iframe style="border: 0 none; width: 100%; height: 100%;"></iframe></div>',

    autoScroll: false,
    styleHtmlContent: true,
    layout: 'fit',

    iFrameLoaded: false,

    initComponent: function () {
        var me = this;

        this.dockedItems = [
            me.getActionButtonContainer()
        ];

        this.callParent(arguments);
    },


    getIframe: function () {
        return this.getTargetEl().down('iframe');
    },


    resizeIframe: function (dimmensions) {
        var iFrame = this.getIframe(),
            widthHasPercentUnit = dimmensions.width.indexOf('%') > -1,
            heightHasPercentUnit = dimmensions.height.indexOf('%') > -1,
            width = widthHasPercentUnit ? this.getWidth() : dimmensions.width,
            height = heightHasPercentUnit ? this.getHeight() : dimmensions.height;

        var animation = iFrame.animate({
            duration: 260,
            to: {
                width: width,
                height: dimmensions.height
            },
            listeners: {
                afteranimate: function () {
                    if (widthHasPercentUnit) {
                        iFrame.setStyle('width', dimmensions.width);
                    }
                    if (heightHasPercentUnit) {
                        iFrame.setStyle('height', dimmensions.height);
                    }
                }
            }
        });
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
        var iFrame = this.getIframe();
        isEdit = isEdit || false;
        if (!Ext.isEmpty(url)) {
            iFrame.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
            this.iFrameLoaded = true;
        } else {
            iFrame.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }

});
