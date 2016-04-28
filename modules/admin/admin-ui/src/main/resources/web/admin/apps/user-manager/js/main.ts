declare var CONFIG;

import "./api.ts";
import {UserAppPanel} from "./app/UserAppPanel";
import {ChangeUserPasswordDialog} from "./app/wizard/ChangeUserPasswordDialog";
import {Router} from "./app/Router";

function getApplication(): api.app.Application {
    var application = new api.app.Application('user-manager', 'Users', 'UM', 'user-manager');
    application.setPath(api.rest.Path.fromString(Router.getPath()));
    application.setWindow(window);
    this.serverEventsListener = new api.app.ServerEventsListener([application]);

    var messageId;
    this.lostConnectionDetector = new api.system.LostConnectionDetector();
    this.lostConnectionDetector.setAuthenticated(true);
    this.lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    });
    this.lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri("");
    });
    this.lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    return application;
}

function startApplication() {

    var application: api.app.Application = getApplication();
    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new UserAppPanel(appBar, application.getPath());

    var body = api.dom.Body.get();
    body.appendChild(appBar);
    body.appendChild(appPanel);

    api.util.AppHelper.preventDragRedirect();

    var changePasswordDialog = new ChangeUserPasswordDialog();
    application.setLoaded(true);
    this.serverEventsListener.start();
    this.lostConnectionDetector.startPolling();

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    };
    api.security.event.PrincipalServerEventsHandler.getInstance().start();
}

window.onload = function () {
    startApplication();
};
