declare var Admin;
declare var CONFIG;

import ModuleSummary = api.module.ModuleSummary;

function startApplication() {
    var application: api.app.Application = api.app.Application.getApplication();
    var appBar = new api.app.AppBar(application);
    var appPanel = new app.ModuleAppPanel(appBar, application.getPath());

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

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
    export var detailPanel: app.browse.ModuleBrowseItemPanel;
}