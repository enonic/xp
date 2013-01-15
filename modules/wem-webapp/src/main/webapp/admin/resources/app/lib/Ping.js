Ext.define('Admin.lib.Ping', {
    singleton: true,

    pingUrl: 'ping.jsp',
    intervalId: -1,
    pollIntervalMs: 5000,
    errorBoxDomId: 'admin-server-status-error-box',
    errorMessageHtml: '<div>Status: {0} - {1}. Could not get a valid response from the server! Please contact srs@enonic.com</div>',


    constructor: function () {
        this.initErrorMessageBox();
    },


    startPolling: function () {
        var me = this;

        me.stopPolling();
        me.doPoll();
        me.intervalId = setInterval(function () {
            me.doPoll();
        }, me.pollIntervalMs);
    },


    stopPolling: function () {
        var me = this;
        clearInterval(me.intervalId)
    },


    doPoll: function () {
        var me = this;
        Ext.Ajax.request({
            url: me.pingUrl,
            success: function (response) {
                // console.log('Ping success: ' + response.responseText);
                if (response.status < 400) {
                    me.hideErrorMessage();
                } else {
                    me.showErrorMessage(response);
                }
            },
            failure: function (response) {
                me.showErrorMessage(response);
            }
        });
    },


    showErrorMessage: function (response) {
        var me = this;
        var messageBox = Ext.fly(me.errorBoxDomId);
        if (messageBox.isVisible()) {
            return;
        }
        var messageHtml = Ext.String.format(me.errorMessageHtml, response.status, response.statusText);
        messageBox.setHTML(messageHtml).show();
    },


    hideErrorMessage: function (response) {
        var me = this;
        Ext.fly(me.errorBoxDomId).hide();
    },


    /**
     * @private
     */
    initErrorMessageBox: function () {
        var me = this,
            dh = Ext.DomHelper;

        var spec = {
            id: me.errorBoxDomId,
            tag: 'div',
            cls: 'admin-server-status-error-box',
            html: 'Server is running'
        };
        dh.append(Ext.getBody(), spec, true).hide();
    }

});