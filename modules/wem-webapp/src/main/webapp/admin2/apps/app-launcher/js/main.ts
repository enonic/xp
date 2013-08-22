///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='app/model/Application.ts' />
///<reference path='app/model/UserStore.ts' />
///<reference path='app/model/Authenticator.ts' />
///<reference path='app/view/HomeBrandingPanel.ts' />
///<reference path='app/view/HomeMainContainerPanel.ts' />
///<reference path='app/view/HomeLinksPanel.ts' />
///<reference path='app/view/AppInfoPanel.ts' />
///<reference path='app/view/LoginFormPanel.ts' />
///<reference path='app/view/AppSelectorPanel.ts' />

declare var Ext;
declare var Admin;
declare var CONFIG;

var description = 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.';
var APPLICATIONS:app_model.Application[] = [
    new app_model.Application('Content Manager', '/admin/resources/images/icons/metro/40x40/database.png', description),
    new app_model.Application('Relationships', '/admin/resources/images/icons/metro/40x40/share.png', description),
    new app_model.Application('Space Admin', '/admin/resources/images/icons/metro/40x40/earth.png', description),
    new app_model.Application('Schema Manager', '/admin/resources/images/icons/metro/40x40/signup.png', description),

    new app_model.Application('Store Manager', '/admin/resources/images/icons/metro/40x40/cart.png', description),
    new app_model.Application('Segment Builder', '/admin/resources/images/icons/metro/40x40/pie.png', description),
    new app_model.Application('Optimizer', '/admin/resources/images/icons/metro/40x40/target.png', description),
    new app_model.Application('Analytics', '/admin/resources/images/icons/metro/40x40/stats.png', description),

    new app_model.Application('Accounts', '/admin/resources/images/icons/metro/40x40/users.png', description),
    new app_model.Application('Modules', '/admin/resources/images/icons/metro/40x40/puzzle.png', description),
    new app_model.Application('Templates', '/admin/resources/images/icons/metro/40x40/insert-template.png', description),
    new app_model.Application('Diagnostics', '/admin/resources/images/icons/metro/40x40/aid.png', description),
];
var USERSTORES:app_model.UserStore[] = [
    new app_model.UserStore('ABC', '1'),
    new app_model.UserStore('LDAP', '2'),
    new app_model.UserStore('Local', '3'),
    new app_model.UserStore('Some very long value', '4')
];

function isUserLoggedIn():bool {
    // TODO create utility class for cookie handling, remove dependency to ExtJs
    var dummyCookie = Ext.util.Cookies.get('dummy_userIsLoggedIn');
    return dummyCookie && dummyCookie === 'true';
}

Ext.application({
    name: 'app-launcher',

    controllers: [
    ],

    launch: function () {
        var userLoggedIn = isUserLoggedIn();
        var mainContainer = new app_view.HomeMainContainerPanel('/admin/rest/ui/background.jpg');
        var homeBrandingPanel = mainContainer.getBrandingPanel();
        api_remote_util.RemoteSystemService.system_getSystemInfo({}, (result:api_remote_util.SystemGetSystemInfoResult) => {
            homeBrandingPanel.setInstallation(result.installationName);
            homeBrandingPanel.setVersion(result.version);
        });

        var linksPanel = new app_view.HomeLinksPanel().
            addLink('Community', 'http://www.enonic.com/community').
            addLink('Documentation', 'http://www.enonic.com/docs').
            addLink('About', 'https://enonic.com/en/home/enonic-cms');

        var appInfoPanel = new app_view.AppInfoPanel();

        var appSelector = new app_view.AppSelectorPanel(APPLICATIONS);
        appSelector.onAppSelected((app:app_model.Application) => {
            appInfoPanel.showAppInfo(app);
        });
        appSelector.onAppDeselected((app:app_model.Application) => {
            appInfoPanel.hideAppInfo();
        });

        var loginPanel = new app_view.LoginFormPanel(new app_model.AuthenticatorImpl());
        loginPanel.setLicensedTo('Licensed to Enonic');
        loginPanel.setUserStores(USERSTORES, USERSTORES[1]);
        loginPanel.onUserAuthenticated((userName:string, userStore:app_model.UserStore) => {
            console.log('User logged in', userName, userStore);
            Ext.util.Cookies.set('dummy_userIsLoggedIn', 'true');
            loginPanel.hide();
            appSelector.show();
        });

        var centerPanel = mainContainer.getCenterPanel();
        centerPanel.appendRightColumn(loginPanel);
        centerPanel.appendRightColumn(appInfoPanel);
        centerPanel.appendRightColumn(linksPanel);
        centerPanel.appendLeftColumn(appSelector);

        if (userLoggedIn) {
            loginPanel.hide();
        } else {
            appSelector.hide();
        }

        api_dom.Body.get().appendChild(mainContainer);
//        Ext.create('Ext.container.Viewport', {
//            id: 'mainViewport',
//            layout: 'border',
//            style: 'border: medium none',
//            padding: 0,
//            items: [
//                {
//                    region: 'center',
//                    bodyCls: 'main-viewport-center-body',
//                    border: 0,
//                    html: '<div id="admin-application-frames" style="height: 100%; width: 100%;"></div>'
//                }
//            ]
//        });
    }
});

