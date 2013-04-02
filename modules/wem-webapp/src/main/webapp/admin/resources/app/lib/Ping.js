Ext.define('Admin.lib.Ping', {
    singleton: true,

    pingUrl: 'ping.jsp',
    intervalId: -1,
    pollIntervalMs: 5000,
    errorMessageId: null,


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
        clearInterval(me.intervalId);
    },


    doPoll: function () {
        var me = this;
        Ext.Ajax.request({
            url: me.pingUrl,
            success: function (response) {
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
        if (!this.errorMessageId) {
            this.errorMessageId = Admin.MessageBus.showError({ lifetime: -1 });
        }
    },


    hideErrorMessage: function (response) {
        if (this.errorMessageId) {
            Admin.MessageBus.removeNotification(this.errorMessageId);
            this.errorMessageId = null;
        }
    }
});