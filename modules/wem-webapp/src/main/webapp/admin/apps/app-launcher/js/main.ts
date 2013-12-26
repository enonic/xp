declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

var USERSTORES: app_login.UserStore[] = [
    new app_login.UserStore('ABC', '1'),
    new app_login.UserStore('LDAP', '2'),
    new app_login.UserStore('Local', '3'),
    new app_login.UserStore('Some very long value', '4')
];

function isUserLoggedIn(): boolean {
    var dummyCookie = api_util.CookieHelper.getCookie('dummy_userIsLoggedIn');
    return dummyCookie === 'true';
}

window.onload = () => {
    var userLoggedIn = isUserLoggedIn();

    var appInfoPanel = new app_launcher.AppInfo();

    var applications = app_launcher.Applications.getAllApps();
    var appSelector = new app_launcher.AppSelector(applications);
    appSelector.addListener({
        onAppHighlighted: (app: app_launcher.Application) => {
            appInfoPanel.showAppInfo(app);
        },
        onAppUnhighlighted: (app: app_launcher.Application) => {
            appInfoPanel.hideAppInfo();
        },
        onAppSelected: (app: app_launcher.Application) => {
            appLauncher.loadApplication(app);
        }
    });

    var linksContainer = new app_home.LinksContainer().
        addLink('Community', 'http://www.enonic.com/community').
        addLink('Documentation', 'http://www.enonic.com/docs').
        addLink('About', 'https://enonic.com/en/home/enonic-cms');

    var loginForm = new app_login.LoginForm(new app_login.AuthenticatorImpl());
    loginForm.setLicensedTo('Licensed to Enonic');
    loginForm.setUserStores(USERSTORES, USERSTORES[1]);
    loginForm.onUserAuthenticated((userName: string, userStore: app_login.UserStore) => {
        console.log('User logged in', userName, userStore);
        api_util.CookieHelper.setCookie('dummy_userIsLoggedIn', 'true');
        loginForm.hide();
        appSelector.show();
        appSelector.afterRender();
    });

    var homeMainContainer = new app_home.HomeMainContainerBuilder().
        setBackgroundImgUrl(api_util.getRestUri('ui/background.jpg')).
        setAppSelector(appSelector).
        setAppInfo(appInfoPanel).
        setLinksContainer(linksContainer).
        setLoginForm(loginForm).
        build();


    if (userLoggedIn) {
        loginForm.hide();
        appSelector.afterRender();
    } else {
        appSelector.hide();
    }

    var appLauncher = new app_launcher.AppLauncher(homeMainContainer);
    api_dom.Body.get().appendChild(homeMainContainer);
    var router = new app_launcher.AppRouter(applications, appLauncher);
    appLauncher.setRouter(router);
};


function appLoaded(appName: string) {
    var app = app_launcher.Applications.getAppById(appName);
    app.setLoaded(true);
}

function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}