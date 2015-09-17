declare var CONFIG;

import Application = api.application.Application;

function startApplication() {
    
    var application: api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new app.ApplicationAppPanel(appBar, application.getPath());

    var body = api.dom.Body.get();
    body.appendChild(appBar);
    body.appendChild(appPanel);

    api.util.AppHelper.preventDragRedirect();

    application.setLoaded(true);

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