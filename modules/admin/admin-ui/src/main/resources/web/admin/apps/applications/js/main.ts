declare var CONFIG;

import "./api.ts";
import {ApplicationAppPanel} from "./app/ApplicationAppPanel";
import {InstallAppDialog} from "./app/installation/InstallAppDialog";
import {InstallAppPromptEvent} from "./app/installation/InstallAppPromptEvent";

import Application = api.application.Application;

function getApplication(): api.app.Application {
    var application = new api.app.Application('applications', 'Applications', 'AM', 'applications');
    application.setPath(api.rest.Path.fromString("/"));
    application.setWindow(window);

    return application;
}

function startLostConnectionDetector() {
    let messageId;
    let lostConnectionDetector = new api.system.LostConnectionDetector();
    lostConnectionDetector.setAuthenticated(true);
    lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    });
    lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri("");
    });
    lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    lostConnectionDetector.startPolling();
}

function startApplication() {

    var application: api.app.Application = getApplication();
    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new ApplicationAppPanel(appBar, application.getPath());

    var body = api.dom.Body.get();
    body.appendChild(appBar);
    body.appendChild(appPanel);

    api.util.AppHelper.preventDragRedirect();

    application.setLoaded(true);

    var serverEventsListener = new api.app.ServerEventsListener([application]);
    serverEventsListener.start();
    
    startLostConnectionDetector();

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    };

    var installAppDialog = new InstallAppDialog();

    InstallAppPromptEvent.on((event) => {
        installAppDialog.updateInstallApplications(event.getInstalledApplications());
        installAppDialog.open();
    });

}

window.onload = function () {
    startApplication();
};
