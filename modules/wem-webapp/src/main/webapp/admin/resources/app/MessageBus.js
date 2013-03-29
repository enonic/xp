Ext.define('Admin.MessageBus', {
    extend: 'Ext.util.Observable',
    singleton: true,

    /**
     * Shows the Admin feedback box
     * @param config {title, message, opts}
     */
    showFeedback: function (config) {
        return window.top.Admin.NotificationManager.notify(config);
    },


    showError: function (message) {
        return window.top.Admin.NotificationManager.error(message);
    },


    showGeneral: function (contentName, resultCallback, publishCallback) {
        return window.top.Admin.NotificationManager.general(contentName, resultCallback, publishCallback);
    },


    showPublish: function (contentName, publishCallback, closeCallback) {
        return window.top.Admin.NotificationManager.publish(contentName, publishCallback, closeCallback);
    },


    removeNotification: function (notificationId) {
        window.top.Admin.NotificationManager.remove(notificationId);
    },


    /**
     * Updates tab count in homescreen tiles
     * @param config {appId, tabCount}
     */
    updateAppTabCount: function (config) {
        var eventName = 'topBar.onUpdateAppTabCount';
        // Make sure the MessageBus in the home frame gets the event.
        if (window.parent) {
            window.parent.Admin.MessageBus.fireEvent(eventName, config);
        }
        this.fireEvent(eventName, config);
    },


    liveEditOpenContent: function (config) {
        this.fireEvent('liveEdit.openContent', config);
    },


    // Just for prototyping purposes
    showLiveEditTestSettingsWindow: function (config) {
        this.fireEvent('liveEdit.showTestSettingsWindow', config);

    }

});