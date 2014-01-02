module app.launcher {

    export class AppLauncher {

        private appManager: api.app.AppManager;

        private homeMainContainer: app.home.HomeMainContainer;

        private adminApplicationFrames: api.dom.DivEl;

        private appIframes: {[name: string]: api.dom.IFrameEl;};

        private lostConnectionDetector: app.launcher.LostConnectionDetector;

        private router: AppRouter;

        constructor(mainContainer: app.home.HomeMainContainer) {
            this.homeMainContainer = mainContainer;
            this.appIframes = {};


            this.adminApplicationFrames = new api.dom.DivEl();
            this.adminApplicationFrames.getEl().setAttribute("style", "overflow-y: hidden;");
            this.adminApplicationFrames.getEl().setHeight('100%').setWidth('100%');

            this.appManager = new api.app.AppManager();
            this.appManager.addListener({
                onShowLauncher: ()=> {
                    this.showLauncherScreen();
                },
                onConnectionLost: ()=> {
                    new api.notify.showError("Lost connection to server - Please wait until connection is restored");
                },
                onConnectionRestored: ()=> {
                }
            });
            this.lostConnectionDetector = new app.launcher.LostConnectionDetector();
            this.lostConnectionDetector.startPolling();
            api.dom.Body.get().appendChild(this.adminApplicationFrames);
        }

        loadApplication(app: Application) {
            if (!app.getAppUrl()) {
                console.warn('Missing URL for app "' + app.getName() + '". Cannot be opened.');
                return;
            }

            api.ui.KeyBindings.get().reset();
            this.homeMainContainer.hide();
//            Applications.getAllApps().forEach((currentApp: Application) => {
//                if (currentApp != app) {
//                    currentApp.hide();
//                }
//            });

            var initial = !app.hasAppFrame();
            var appFrame: api.dom.IFrameEl = app.getAppFrame();
            if (!initial) {
                appFrame.show();
            }
            else {
                this.adminApplicationFrames.appendChild(appFrame);
                this.showLoadMask();
            }
            var type = api.app.AppLauncherEventType.Show;
            appFrame.postMessage(<api.app.AppLauncherEvent>{appLauncherEvent: api.app.AppLauncherEventType[type]});
        }

        showLauncherScreen() {
            Applications.getAllApps().forEach((app: Application) => {
                app.hide();
            });
            api.ui.KeyBindings.get().reset();
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
