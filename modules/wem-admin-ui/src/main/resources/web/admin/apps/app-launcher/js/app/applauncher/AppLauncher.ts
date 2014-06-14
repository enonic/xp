module app.launcher {

    export class AppLauncher {

        private appManager: api.app.AppManager;

        private homeMainContainer: app.home.HomeMainContainer;

        private adminApplicationFrames: api.dom.DivEl;

        private lostConnectionDetector: app.launcher.LostConnectionDetector;

        private router: AppRouter;

        private loadMask: api.ui.LoadMask;

        private currentApplication: api.app.Application;

        private currentApplicationHash: string;

        constructor(mainContainer: app.home.HomeMainContainer) {
            this.homeMainContainer = mainContainer;

            this.adminApplicationFrames = new api.dom.DivEl("applications-frame");

            this.loadMask = new api.ui.LoadMask(this.adminApplicationFrames);

            this.appManager = new api.app.AppManager();
            this.appManager.onShowLauncher(()=> {
                this.showLauncherScreen();
            });
            var messageId;
            this.appManager.onConnectionLost(()=> {
                messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
            });

            this.appManager.onConnectionRestored(() => {
                api.notify.NotifyManager.get().hide(messageId);
            });

            this.lostConnectionDetector = new app.launcher.LostConnectionDetector();
            if (CONFIG.baseUri.search('localhost') == -1) {
                this.lostConnectionDetector.startPolling();
            } else {
                console.log("LostConnectionDetector disabled when client runs against localhost");
            }
            api.dom.Body.get().appendChild(this.adminApplicationFrames);

            api.app.ShowAppLauncherEvent.on((event) => {
                this.currentApplication = event.getApplication();
                this.currentApplicationHash = hasher.getHash();
                Applications.getAllApps().forEach((app: api.app.Application) => {
                    if (app != event.getApplication()) {
                        app.hide();
                    }
                });
                this.homeMainContainer.setBackgroundImgUrl("");
                this.homeMainContainer.enableReturnButton();
            });

            app.home.ReturnToAppEvent.on(() => {
                this.homeMainContainer.hide();
                hasher.setHash(this.currentApplicationHash);
            });
        }

        loadApplication(application: api.app.Application) {

            Applications.getAllApps().forEach((app: api.app.Application) => {
                if (app != application) {
                    app.hide();
                }
            });

            if (!application.getAppUrl()) {
                console.warn('Missing URL for app "' + application.getName() + '". Cannot be opened.');
                return;
            }

            api.ui.KeyBindings.get().reset();
            this.homeMainContainer.hide();

            var appFrame: api.dom.IFrameEl = application.getAppFrame();
            if (application.isLoaded()) {
                appFrame.show();
            }
            else {
                this.adminApplicationFrames.appendChild(appFrame);
                this.loadMask.show();
                application.onLoaded(() => this.loadMask.hide());
            }
            var type = api.app.AppLauncherEventType.Show;
            appFrame.postMessage(<api.app.AppLauncherEvent>{appLauncherEvent: api.app.AppLauncherEventType[type]});
        }

        showLauncherScreen() {
            if (this.loadMask.isVisible()) {
                this.loadMask.hide();
            }
            api.ui.KeyBindings.get().reset();
            this.homeMainContainer.show();
            this.homeMainContainer.giveFocus();
            hasher.setHash(AppRouter.HOME_HASH_ID);
        }

        setRouter(router: AppRouter) {
            this.router = router;
        }
    }
}
