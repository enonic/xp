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

    showLiveEdit: function (config) {
        this.fireEvent('liveEditWindow.show', config);
    }

});