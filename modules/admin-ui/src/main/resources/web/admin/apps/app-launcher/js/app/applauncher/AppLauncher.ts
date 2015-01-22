module app.launcher {

    export class AppLauncher {

        private appManager: api.app.AppManager;

        private homeMainContainer: app.home.HomeMainContainer;

        private adminApplicationFrames: api.dom.DivEl;

        private router: AppRouter;

        private loadMask: api.ui.mask.LoadMask;

        private currentApplication: api.app.Application;

        private currentApplicationHash: string;

        constructor(mainContainer: app.home.HomeMainContainer) {
            this.homeMainContainer = mainContainer;

            var returnToAppAction = new api.ui.Action("Return");
            returnToAppAction.onExecuted(() => {
                this.returnToApp();
            });

            this.homeMainContainer.setReturnAction(returnToAppAction);

            this.adminApplicationFrames = new api.dom.DivEl("applications-frame");

            this.loadMask = new api.ui.mask.LoadMask(this.adminApplicationFrames);

            this.appManager = new api.app.AppManager();
            var messageId;
            this.appManager.onConnectionLost(()=> {
                messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
            });

            this.appManager.onConnectionRestored(() => {
                api.notify.NotifyManager.get().hide(messageId);
            });

            api.dom.Body.get().appendChild(this.adminApplicationFrames);

            api.app.bar.event.ShowAppLauncherEvent.on((event) => {
                api.ui.KeyBindings.get().shelveBindings();

                this.currentApplication = event.getApplication();
                this.currentApplicationHash = hasher.getHash();
                this.homeMainContainer.setBackgroundImgUrl("");
                this.homeMainContainer.enableReturnButton();
                this.homeMainContainer.disableBranding();
                this.homeMainContainer.giveFocus();

                Applications.getAllApps().forEach((app: api.app.Application) => {
                    if (app != event.getApplication()) {
                        app.hide();
                    }
                });

                this.showLauncherScreen();

                if (event.isSessionExpired()) {
                    new app.home.LogOutEvent().fire();
                }
            });

            app.home.LogOutEvent.on(() => {
                Applications.getAllApps().forEach((app: api.app.Application) => {
                    app.getAppFrame().remove();
                });
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
                appFrame.giveFocus();
            } else {
                this.adminApplicationFrames.appendChild(appFrame);
                this.loadMask.show();
                application.onLoaded(() => {
                    this.loadMask.hide();
                    appFrame.giveFocus();
                });
            }
            var type = api.app.AppLauncherEventType.Show;
            appFrame.postMessage(<api.app.AppLauncherEvent>{appLauncherEvent: api.app.AppLauncherEventType[type]});
        }

        showLauncherScreen() {
            if (this.loadMask.isVisible()) {
                this.loadMask.hide();
            }

            this.homeMainContainer.show();
            this.homeMainContainer.giveFocus();

            api.ui.KeyBindings.get().bindKey(new api.ui.KeyBinding("esc", () => {
                this.returnToApp();
            }));

            hasher.setHash(AppRouter.HOME_HASH_ID);
        }

        private returnToApp() {
            this.homeMainContainer.hide();
            api.ui.KeyBindings.get().unshelveBindings();
            this.currentApplication.getAppFrame().giveFocus();
            hasher.setHash(this.currentApplicationHash);
        }

        setRouter(router: AppRouter) {
            this.router = router;
        }
    }
}
