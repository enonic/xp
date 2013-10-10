///<reference path='../../../api/js/lib/ExtJs.d.ts' />
///<reference path='../../../api/js/lib/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />
///<reference path='../../../api/js/lib/crossroads.d.ts' />
///<reference path='../../../api/js/lib/hasher.d.ts' />

///<reference path='app/home/Branding.ts' />
///<reference path='app/home/CenterPanel.ts' />
///<reference path='app/home/HomeMainContainer.ts' />
///<reference path='app/home/LinksContainer.ts' />
///<reference path='app/home/InstallationInfo.ts' />
///<reference path='app/home/VersionInfo.ts' />
///<reference path='app/applauncher/Application.ts' />
///<reference path='app/applauncher/Applications.ts' />
///<reference path='app/applauncher/AppTile.ts' />
///<reference path='app/applauncher/AppInfo.ts' />
///<reference path='app/applauncher/AppSelectorListener.ts' />
///<reference path='app/applauncher/AppSelector.ts' />
///<reference path='app/applauncher/AppLauncher.ts' />
///<reference path='app/applauncher/LostConnectionDetector.ts' />
///<reference path='app/applauncher/LostConnectionDetectorListener.ts' />
///<reference path='app/applauncher/AppRouter.ts' />
///<reference path='app/login/LoginForm.ts' />
///<reference path='app/login/UserStore.ts' />
///<reference path='app/login/Authenticator.ts' />

declare var Ext;
declare var Admin;
declare var CONFIG;

var USERSTORES:app_login.UserStore[] = [
    new app_login.UserStore('ABC', '1'),
    new app_login.UserStore('LDAP', '2'),
    new app_login.UserStore('Local', '3'),
    new app_login.UserStore('Some very long value', '4')
];

function isUserLoggedIn():boolean {
    var dummyCookie = api_util.CookieHelper.getCookie('dummy_userIsLoggedIn');
    return dummyCookie === 'true';
}

window.onload = () => {
    var userLoggedIn = isUserLoggedIn();
    var mainContainer = new app_home.HomeMainContainer(api_util.getRestUri('ui/background.jpg'));
    var appLauncher = new app_launcher.AppLauncher(mainContainer);

    var homeBrandingPanel = mainContainer.getBrandingPanel();
    api_remote_util.RemoteSystemService.system_getSystemInfo({}, (result:api_remote_util.SystemGetSystemInfoResult) => {
        homeBrandingPanel.setInstallation(result.installationName);
        homeBrandingPanel.setVersion(result.version);
    });

    var linksPanel = new app_home.LinksContainer().
        addLink('Community', 'http://www.enonic.com/community').
        addLink('Documentation', 'http://www.enonic.com/docs').
        addLink('About', 'https://enonic.com/en/home/enonic-cms');

    var appInfoPanel = new app_launcher.AppInfo();

    var applications = app_launcher.Applications.getAllApps();
    var appSelector = new app_launcher.AppSelector(applications);
    appSelector.addListener({
        onAppHighlighted: (app:app_launcher.Application) => {
            appInfoPanel.showAppInfo(app);
        },
        onAppUnhighlighted: (app:app_launcher.Application) => {
            appInfoPanel.hideAppInfo();
        },
        onAppSelected: (app:app_launcher.Application) => {
            appLauncher.loadApplication(app);
        }
    });

    var loginPanel = new app_login.LoginForm(new app_login.AuthenticatorImpl());
    loginPanel.setLicensedTo('Licensed to Enonic');
    loginPanel.setUserStores(USERSTORES, USERSTORES[1]);
    loginPanel.onUserAuthenticated((userName:string, userStore:app_login.UserStore) => {
        console.log('User logged in', userName, userStore);
        api_util.CookieHelper.setCookie('dummy_userIsLoggedIn', 'true');
        loginPanel.hide();
        appSelector.show();
        appSelector.afterRender();
    });

    var centerPanel = mainContainer.getCenterPanel();
    centerPanel.appendRightColumn(loginPanel);
    centerPanel.appendRightColumn(appInfoPanel);
    centerPanel.appendRightColumn(linksPanel);
    centerPanel.appendLeftColumn(appSelector);

    if (userLoggedIn) {
        loginPanel.hide();
        appSelector.afterRender();
    } else {
        appSelector.hide();
    }

    api_dom.Body.get().appendChild(mainContainer);

    new app_launcher.AppRouter(applications, appLauncher);
}
