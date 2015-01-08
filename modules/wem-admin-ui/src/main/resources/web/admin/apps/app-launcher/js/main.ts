declare var Admin;
declare var CONFIG;

function isUserLoggedIn(): boolean {
    var dummyCookie = api.util.CookieHelper.getCookie('dummy.userIsLoggedIn');
    return dummyCookie === 'true';
}

function startApplication() {
    var userLoggedIn = isUserLoggedIn();

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
        console.log('User logged in', user.getDisplayName(), user.getKey().toString());
        api.util.CookieHelper.setCookie('dummy.userIsLoggedIn', 'true');
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


    if (userLoggedIn) {
        homeMainContainer.showAppSelector();
        serverEventsListener.start();
    } else {
        homeMainContainer.showLogin();
    }

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
}

function getApplication(id: string): api.app.Application {
    return app.launcher.Applications.getAppById(id);
}

function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}