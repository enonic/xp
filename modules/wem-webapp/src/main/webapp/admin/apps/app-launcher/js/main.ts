declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

var USERSTORES: app.login.UserStore[] = [
    new app.login.UserStore('ABC', '1'),
    new app.login.UserStore('LDAP', '2'),
    new app.login.UserStore('Local', '3'),
    new app.login.UserStore('Some very long value', '4')
];

function isUserLoggedIn(): boolean {
    var dummyCookie = api.util.CookieHelper.getCookie('dummy.userIsLoggedIn');
    return dummyCookie === 'true';
}

window.onload = () => {
    var userLoggedIn = isUserLoggedIn();

    var appInfoPanel = new app.launcher.AppInfo();

    var applications = app.launcher.Applications.getAllApps();
    var appSelector = new app.launcher.AppSelector(applications);
    appSelector.onAppHighlighted((event: app.launcher.AppHighlightedEvent) => {
        appInfoPanel.showAppInfo(event.getApplication());
    });
    appSelector.onAppUnhighlighted(() => {
        appInfoPanel.hideAppInfo();
    });
    appSelector.onAppSelected((event: app.launcher.AppSelectedEvent) => {
        appLauncher.loadApplication(event.getApplication());
    });

    var linksContainer = new app.home.LinksContainer().
        addLink('Community', 'http://www.enonic.com/community').
        addLink('Documentation', 'http://www.enonic.com/docs').
        addLink('About', 'https://enonic.com/en/home/enonic-cms');

    var loginForm = new app.login.LoginForm(new app.login.AuthenticatorImpl());
    loginForm.setLicensedTo('Licensed to Enonic');
    loginForm.setUserStores(USERSTORES, USERSTORES[1]);
    loginForm.onUserAuthenticated((userName: string, userStore: app.login.UserStore) => {
        console.log('User logged in', userName, userStore);
        api.util.CookieHelper.setCookie('dummy.userIsLoggedIn', 'true');
        loginForm.hide();
        appSelector.show();
    });

    var homeMainContainer = new app.home.HomeMainContainerBuilder().
        setBackgroundImgUrl(api.util.getRestUri('ui/background.jpg')).
        setAppSelector(appSelector).
        setAppInfo(appInfoPanel).
        setLinksContainer(linksContainer).
        setLoginForm(loginForm).
        build();


    if (userLoggedIn) {
        loginForm.hide();
    } else {
        appSelector.hide();
    }

    var appLauncher = new app.launcher.AppLauncher(homeMainContainer);
    api.dom.Body.get().appendChild(homeMainContainer);
    var router = new app.launcher.AppRouter(applications, appLauncher);
    appLauncher.setRouter(router);
};


function appLoaded(appName: string) {
    var loadedApp = app.launcher.Applications.getAppById(appName);
    loadedApp.setLoaded(true);
}

function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}