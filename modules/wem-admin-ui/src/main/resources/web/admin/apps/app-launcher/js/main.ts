declare var Admin;
declare var CONFIG;

var USERSTORES: api.security.UserStore[] = [
    new api.security.UserStore('ABC', '1'),
    new api.security.UserStore('LDAP', '2'),
    new api.security.UserStore('Local', '3'),
    new api.security.UserStore('Some very long value', '4')
];

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
    loginForm.setUserStores(USERSTORES, USERSTORES[1]);
    loginForm.onUserAuthenticated((userName: string, userStore: api.security.UserStore) => {
        console.log('User logged in', userName, userStore);
        api.util.CookieHelper.setCookie('dummy.userIsLoggedIn', 'true');
        homeMainContainer.showAppSelector();
    });

    var homeMainContainer = new app.home.HomeMainContainerBuilder().
        setBackgroundImgUrl(api.util.UriHelper.getRestUri('ui/background.jpg')).
        setAppSelector(appSelector).
        setLinksContainer(linksContainer).
        setLoginForm(loginForm).
        build();


    if (userLoggedIn) {
        homeMainContainer.showAppSelector();
    } else {
        homeMainContainer.showLogin();
    }

    var appLauncher = new app.launcher.AppLauncher(homeMainContainer);
    api.dom.Body.get().appendChild(homeMainContainer);
    var router = new app.launcher.AppRouter(applications, appLauncher);
    appLauncher.setRouter(router);

    var serverEventsListener = new api.app.ServerEventsListener(applications);
    var managerInstance = api.app.AppManager.instance();
    serverEventsListener.onConnectionLost(() => {
        managerInstance.notifyConnectionLost();
    });
    serverEventsListener.onConnectionRestored(() => {
        managerInstance.notifyConnectionRestored();
    });
    serverEventsListener.start();
}

function getApplication(id: string): api.app.Application {
    return app.launcher.Applications.getAppById(id);
}

function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}