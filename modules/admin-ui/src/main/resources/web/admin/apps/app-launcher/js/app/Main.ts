module app {

    export class Main {

        private appLauncher: app.launcher.AppLauncher;
        private appSelector: app.launcher.AppSelector;
        private homeMainContainer: app.home.HomeMainContainer;
        private serverEventsListener: api.app.ServerEventsListener;
        private appManager: api.app.AppManager;

        constructor() {

        }

        start() {
            var allApplications: api.app.Application[] = app.launcher.Applications.getAllApps();
            this.serverEventsListener = new api.app.ServerEventsListener(allApplications);

            this.appSelector = new app.launcher.AppSelector(allApplications);
            this.appSelector.onAppSelected((event: app.launcher.AppSelectedEvent) => {
                this.appLauncher.loadApplication(event.getApplication());
            });

            this.homeMainContainer = this.createHomeMainContainer();
            this.appLauncher = new app.launcher.AppLauncher(this.homeMainContainer);
            this.homeMainContainer.hide();
            api.dom.Body.get().appendChild(this.homeMainContainer);

            var router = new app.launcher.AppRouter(allApplications, this.appLauncher);

            this.appManager = api.app.AppManager.instance();
            this.serverEventsListener.onConnectionLost(this.onConnectionLost.bind(this));
            this.serverEventsListener.onConnectionRestored(this.onConnectionRestored.bind(this));

            app.home.LogOutEvent.on(this.onLogout.bind(this));

            this.initialIsAuthenticatedCheck();
        }

        private initialIsAuthenticatedCheck() {
            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                if (loginResult.isAuthenticated()) {
                    this.onUserAuthenticated(loginResult);
                } else {
                    this.homeMainContainer.showLogin();
                }
            }).catch((reason: any) => {
                this.homeMainContainer.showLogin();
            }).done();
        }

        private createHomeMainContainer(): app.home.HomeMainContainer {
            var linksContainer = new app.home.LinksContainer().
                addLink('Community', 'http://www.enonic.com/community').
                addLink('Documentation', 'http://www.enonic.com/docs').
                addLink('About', 'https://enonic.com/en/home/enonic-cms');

            var loginForm = new app.login.LoginForm(new app.login.AuthenticatorImpl());
            loginForm.onUserAuthenticated(this.onUserAuthenticated.bind(this));

            return new app.home.HomeMainContainerBuilder().
                setAppSelector(this.appSelector).
                setLinksContainer(linksContainer).
                setLoginForm(loginForm).
                build();
        }

        private onLogout() {
            this.serverEventsListener.stop();
            this.appSelector.setAllowedApps([]);
            this.appLauncher.setAllowedApps([]);
            this.appManager.notifyConnectionRestored();

            app.launcher.Applications.getAllApps().forEach((app: api.app.Application) => {
                app.getAppFrame().remove();
                app.setOpenTabs(0);
            });
        }

        private onUserAuthenticated(loginResult: api.security.auth.LoginResult) {
            var allowedApps = app.launcher.Applications.getAppsByIds(loginResult.getApplications());
            this.appSelector.setAllowedApps(allowedApps);
            this.appLauncher.setAllowedApps(allowedApps);
            new app.home.LogInEvent(loginResult.getUser()).fire();
            this.homeMainContainer.showAppSelector();
            this.serverEventsListener.start();
        }

        private onConnectionRestored() {
            this.appManager.notifyConnectionRestored();
        }

        private onConnectionLost() {
            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                if (!loginResult.isAuthenticated()) {
                    this.onLogout();
                    this.homeMainContainer.showLogin();
                    this.appLauncher.showLauncherScreen();
                }

            }).catch((reason: any) => {
                this.appManager.notifyConnectionLost();
            }).done();
        }
    }

}