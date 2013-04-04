Ext.define('Admin.MessageBus', {
    extend: 'Ext.util.Observable',
    singleton: true,

    /**
     * Shows the Admin feedback box
     * @param config {title, message, opts}
     */
    showFeedback: function (config) {
        window.top.Admin.MessageBus.fireEvent('showNotification', 'notify', config);
    },


    showError: function (message) {
        window.top.Admin.MessageBus.fireEvent('showNotification', 'error', message);
    },


    showGeneral: function (contentName, resultCallback, publishCallback) {
        window.top.Admin.MessageBus.fireEvent('showNotification', 'general', {
            contentName: contentName,
            resultCallback: resultCallback,
            publishCallback: publishCallback
        });
    },


    showPublish: function (contentName, publishCallback, closeCallback) {
        window.top.Admin.MessageBus.fireEvent('showNotification', 'publish', {
            contentName: contentName,
            publishCallback: publishCallback,
            closeCallback: closeCallback
        });
    },


    removeNotification: function (mark) {
        window.top.Admin.MessageBus.fireEvent('removeNotification', mark);
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


    liveEditOpenContent: function (contentModel) {
        this.fireEvent('liveEdit.openContent', contentModel);
    },


    // Just for prototyping purposes
    showLiveEditTestSettingsWindow: function (config) {
        this.fireEvent('liveEdit.showTestSettingsWindow', config);

    }

});