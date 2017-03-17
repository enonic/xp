declare var CONFIG;

import './api.ts';
import {UserAppPanel} from './app/UserAppPanel';
import {ChangeUserPasswordDialog} from './app/wizard/ChangeUserPasswordDialog';
import {Router} from './app/Router';

function getApplication(): api.app.Application {
    let application = new api.app.Application('user-manager', 'Users', 'UM', CONFIG.appIconUrl);
    application.setPath(api.rest.Path.fromString(Router.getPath()));
    application.setWindow(window);

    return application;
}

function startLostConnectionDetector() {
    let messageId;
    let lostConnectionDetector = new api.system.LostConnectionDetector();
    lostConnectionDetector.setAuthenticated(true);
    lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError('Lost connection to server - Please wait until connection is restored', false);
    });
    lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri('');
    });
    lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    lostConnectionDetector.startPolling();
}

function startApplication() {

    let application: api.app.Application = getApplication();
    let appBar = new api.app.bar.TabbedAppBar(application);
    appBar.setHomeIconAction();
    let appPanel = new UserAppPanel(appBar, application.getPath());

    let body = api.dom.Body.get();
    body.appendChild(appBar);
    body.appendChild(appPanel);

    api.util.AppHelper.preventDragRedirect();

    let changePasswordDialog = new ChangeUserPasswordDialog();
    application.setLoaded(true);

    let serverEventsListener = new api.app.ServerEventsListener([application]);
    serverEventsListener.start();

    startLostConnectionDetector();

    api.security.event.PrincipalServerEventsHandler.getInstance().start();
}

window.onload = function () {
    startApplication();
};
