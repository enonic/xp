module app {

    export class Main {

        private appLauncher: app.launcher.AppLauncher;
        private appSelector: app.launcher.AppSelector;
        private homeMainContainer: app.home.HomeMainContainer;
        private loginForm: app.login.LoginForm;
        private serverEventsListener: api.app.ServerEventsListener;
        private lostConnectionDetector: app.launcher.LostConnectionDetector;
        private appManager: api.app.AppManager;
        private authenticated: boolean = false;
        private connectionLost: boolean = false;

        constructor() {
            this.lostConnectionDetector = new app.launcher.LostConnectionDetector();
        }

        start() {
            var allApplications: api.app.Application[] = app.launcher.Applications.getAllApps();
            this.serverEventsListener = new api.app.ServerEventsListener(allApplications);

            this.appSelector = new app.launcher.AppSelector(allApplications);
            this.appSelector.onAppSelected((event: app.launcher.AppSelectedEvent) => {
                this.appLauncher.showApplication(event.getApplication());
            });

            this.homeMainContainer = this.createHomeMainContainer();
            this.appLauncher = new app.launcher.AppLauncher(this.homeMainContainer);
            this.homeMainContainer.hide();
            api.dom.Body.get().appendChild(this.homeMainContainer);

            var router = new app.launcher.AppRouter(allApplications, this.appLauncher);

            this.appManager = api.app.AppManager.instance();
            this.serverEventsListener.onConnectionLost(this.onConnectionLost.bind(this));
            this.serverEventsListener.onConnectionRestored(this.onConnectionRestored.bind(this));
            this.lostConnectionDetector.onConnectionLost(this.onConnectionLost.bind(this));
            this.lostConnectionDetector.onConnectionRestored(this.onConnectionRestored.bind(this));
            this.lostConnectionDetector.onSessionExpired(this.handleSessionExpired.bind(this));

            app.home.LogOutEvent.on(this.onLogout.bind(this));

            this.initialIsAuthenticatedCheck();
        }

        private initialIsAuthenticatedCheck() {
            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                if (loginResult.isAuthenticated()) {
                    this.authenticated = true;
                    this.lostConnectionDetector.setAuthenticated(true);
                    this.onUserAuthenticated(loginResult);
                } else {
                    this.lostConnectionDetector.setAuthenticated(false);
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

            this.loginForm = new app.login.LoginForm(new app.login.AuthenticatorImpl());
            this.loginForm.onUserAuthenticated(this.onUserAuthenticated.bind(this));

            return new app.home.HomeMainContainerBuilder().
                setAppSelector(this.appSelector).
                setLinksContainer(linksContainer).
                setLoginForm(this.loginForm).
                build();
        }

        private onLogout() {
            this.authenticated = false;
            this.connectionLost = false;
            this.serverEventsListener.stop();
            this.lostConnectionDetector.stopPolling();
            this.appSelector.setAllowedApps([]);
            this.appLauncher.setAllowedApps([]);
            this.appManager.notifyConnectionRestored();

            app.launcher.Applications.getAllApps().forEach((app: api.app.Application) => {
                app.getAppFrame().remove();
                app.setOpenTabs(0);
            });
        }

        private onUserAuthenticated(loginResult: api.security.auth.LoginResult) {
            this.lostConnectionDetector.setAuthenticated(loginResult.isAuthenticated());
            var allowedApps = app.launcher.Applications.getAppsByIds(loginResult.getApplications());
            this.appSelector.setAllowedApps(allowedApps);
            this.appLauncher.setAllowedApps(allowedApps);
            new app.home.LogInEvent(loginResult.getUser()).fire();
            this.homeMainContainer.showAppSelector();
            this.serverEventsListener.start();
            this.lostConnectionDetector.startPolling();
        }

        private onConnectionRestored() {
            this.appManager.notifyConnectionRestored();
            this.connectionLost = false;
        }

        private onConnectionLost() {
            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                this.lostConnectionDetector.setAuthenticated(loginResult.isAuthenticated());
                if (this.authenticated && !loginResult.isAuthenticated()) {
                    this.handleSessionExpired();
                }

            }).catch((reason: any) => {
                if (!this.connectionLost) {
                    this.connectionLost = true;
                    this.appManager.notifyConnectionLost();
                }
            }).done();
        }

        private handleSessionExpired() {
            this.onLogout();
            this.homeMainContainer.showLogin();
            this.appLauncher.showLauncherScreen();

            this.loginForm.setMessage('Your session has expired.');
        }
    }

}