module app_launcher {

    export class AppLauncher {

        private appManager: api_app.AppManager;

        private homeMainContainer: app_home.HomeMainContainer;

        private adminApplicationFrames: api_dom.DivEl;

        private appIframes: {[name: string]: api_dom.IFrameEl;};

        private lostConnectionDetector: app_launcher.LostConnectionDetector;

        private router: AppRouter;

        constructor(mainContainer: app_home.HomeMainContainer) {
            this.homeMainContainer = mainContainer;
            this.appIframes = {};


            this.adminApplicationFrames = new api_dom.DivEl();
            this.adminApplicationFrames.getEl().setAttribute("style", "overflow-y: hidden;");
            this.adminApplicationFrames.getEl().setHeight('100%').setWidth('100%');

            this.appManager = new api_app.AppManager();
            this.appManager.addListener({
                onShowLauncher: ()=> {
                    this.showLauncherScreen();
                },
                onConnectionLost: ()=> {
                    new api_notify.showError("Lost connection to server - Please wait until connection is restored");
                },
                onConnectionRestored: ()=> {
                }
            });
            this.lostConnectionDetector = new app_launcher.LostConnectionDetector();
            this.lostConnectionDetector.startPolling();
            api_dom.Body.get().appendChild(this.adminApplicationFrames);
        }

        loadApplication(app: Application) {
            if (!app.getAppUrl()) {
                console.warn('Missing URL for app "' + app.getName() + '". Cannot be opened.');
                return;
            }

            api_ui.KeyBindings.get().reset();
            this.homeMainContainer.hide();
            Applications.getAllApps().forEach((currentApp: Application) => {
                if (currentApp != app) {
                    currentApp.hide();
                }
            });

            var initial = !app.hasAppFrame();
            var appFrame: api_dom.IFrameEl = app.getAppFrame();
            if (!initial) {
                appFrame.show();
            }
            else {
                this.adminApplicationFrames.appendChild(appFrame);
                this.showLoadMask();
            }
            var type = api_app.AppLauncherEventType.Show;
            appFrame.postMessage(<api_app.AppLauncherEvent>{appLauncherEvent: api_app.AppLauncherEventType[type]});
        }

        showLauncherScreen() {
            Applications.getAllApps().forEach((app: Application) => {
                app.hide();
            });
            api_ui.KeyBindings.get().reset();
            this.homeMainContainer.show();
            this.homeMainContainer.giveFocus();
            hasher.setHash(AppRouter.HOME_HASH_ID);
        }

        private showLoadMask() {
            // TODO implement loadMask
        }

        setRouter(router: AppRouter) {
            this.router = router;
        }
    }
}
