declare var CONFIG;

import Application = api.application.Application;


var application = (function () {
    var application = new api.app.Application('applications', 'Applications', 'AM', 'puzzle');
    application.setPath(api.rest.Path.fromString("/"));
    application.setWindow(window);
    this.serverEventsListener = new api.app.ServerEventsListener([application]);
    return application;
})();

function getApplication(id: string): api.app.Application {
    return application;
}

function startApplication() {

    var application: api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new app.ApplicationAppPanel(appBar, application.getPath());

    var body = api.dom.Body.get();
    body.appendChild(appBar);
    body.appendChild(appPanel);

    api.util.AppHelper.preventDragRedirect();

    application.setLoaded(true);

    if (this.serverEventsListener) {
        this.serverEventsListener.start();
    }

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    };

}

module components {
    export var detailPanel: app.browse.ApplicationBrowseItemPanel;
}