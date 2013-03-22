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


    liveEditOpenContent: function (config) {
        this.fireEvent('liveEdit.openContent', config);
    },


    // Just for prototyping purposes
    showLiveEditTestSettingsWindow: function (config) {
        this.fireEvent('liveEdit.showTestSettingsWindow', config);
    }

});