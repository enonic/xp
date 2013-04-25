var admin;
(function (admin) {
    (function (api) {
        (function (message) {
            var messageBus = Ext.create('Ext.util.Observable');

            function showFeedback(message) {
                messageBus.fireEvent('showNotification', 'notify', message);
            }

            message.showFeedback = showFeedback;
            function updateAppTabCount(appId, tabCount) {
                var eventName = 'topBar.onUpdateAppTabCount';
                var config = {
                    appId: appId,
                    tabCount: tabCount
                };
                messageBus.fireEvent(eventName, config);
            }

            message.updateAppTabCount = updateAppTabCount;
            function addListener(name, func, scope) {
                messageBus.addListener(name, func, scope);
            }

            message.addListener = addListener;
        })(api.message || (api.message = {}));
        var message = api.message;
    })(admin.api || (admin.api = {}));
    var api = admin.api;
})(admin || (admin = {}));
Ext.define('Admin.MessageBus', {
    extend: 'Ext.util.Observable',
    singleton: true,
    liveEditOpenContent: function () {
        this.fireEvent('liveEdit.openContent');
    },
    showLiveEditTestSettingsWindow: function (config) {
        this.fireEvent('liveEdit.showTestSettingsWindow', config);
    }
});
var admin;
(function (admin) {
})(admin || (admin = {}));
//@ sourceMappingURL=api.js.map
