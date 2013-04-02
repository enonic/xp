Ext.define('Admin.MessageBus', {
    extend: 'Ext.util.Observable',
    singleton: true,

    /**
     * Shows the Admin feedback box
     * @param config {title, message, opts}
     */
    showFeedback: function (config) {
        this.fireEvent('feedbackBox.show', config);
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