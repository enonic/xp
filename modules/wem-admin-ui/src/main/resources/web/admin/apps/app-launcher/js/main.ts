declare var Admin;
declare var CONFIG;

function startApplication() {

    var applications: api.app.Application[] = app.launcher.Applications.getAllApps();
    var appSelector = new app.launcher.AppSelector(applications);
    appSelector.onAppSelected((event: app.launcher.AppSelectedEvent) => {
        appLauncher.loadApplication(event.getApplication());
    });

    var linksContainer = new app.home.LinksContainer().
        addLink('Community', 'http://www.enonic.com/community').
        addLink('Documentation', 'http://www.enonic.com/docs').
        addLink('About', 'https://enonic.com/en/home/enonic-cms');

    var loginForm = new app.login.LoginForm(new app.login.AuthenticatorImpl());

    var serverEventsListener = new api.app.ServerEventsListener(applications);
    loginForm.onUserAuthenticated((user: api.security.User) => {
        new app.home.LogInEvent(user).fire();
        console.log('User logged in', user.getDisplayName(), user.getKey().toString());
        homeMainContainer.showAppSelector();
        serverEventsListener.start();
    });
    app.home.LogOutEvent.on(() => {
        serverEventsListener.stop();
    });

    var homeMainContainer = new app.home.HomeMainContainerBuilder().
        setBackgroundImgUrl(api.util.UriHelper.getRestUri('ui/background.jpg')).
        setAppSelector(appSelector).
        setLinksContainer(linksContainer).
        setLoginForm(loginForm).
        build();


    var appLauncher = new app.launcher.AppLauncher(homeMainContainer);
    api.dom.Body.get().appendChild(homeMainContainer);
    var router = new app.launcher.AppRouter(applications, appLauncher);
    appLauncher.setRouter(router);

    var managerInstance = api.app.AppManager.instance();
    serverEventsListener.onConnectionLost(() => {
        managerInstance.notifyConnectionLost();
    });
    serverEventsListener.onConnectionRestored(() => {
        managerInstance.notifyConnectionRestored();
    });

    new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
        if (loginResult.isAuthenticated()) {
            new app.home.LogInEvent(loginResult.getUser()).fire();
            homeMainContainer.showAppSelector();
            serverEventsListener.start();
        } else {
            homeMainContainer.showLogin();
        }
    }).catch((reason: any) => {
        homeMainContainer.showLogin();
    }).done();
}

function getApplication(id: string): api.app.Application {
    return app.launcher.Applications.getAppById(id);
}

function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}