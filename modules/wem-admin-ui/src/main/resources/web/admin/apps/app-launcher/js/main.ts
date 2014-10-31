declare var Admin;
declare var CONFIG;

var USERSTORES: api.security.UserStore[] = [
    api.security.UserStore.create().setDisplayName('System').setKey('system').build(),
    api.security.UserStore.create().setDisplayName('LDAP').setKey('2').build(),
    api.security.UserStore.create().setDisplayName('Local').setKey('3').build(),
    api.security.UserStore.create().setDisplayName('Some very long value').setKey('4').build()
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
    loginForm.setUserStores(USERSTORES, USERSTORES[0]);
    loginForm.onUserAuthenticated((user: api.security.User) => {
        console.log('User logged in', user.getDisplayName(), user.getKey().toString());
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