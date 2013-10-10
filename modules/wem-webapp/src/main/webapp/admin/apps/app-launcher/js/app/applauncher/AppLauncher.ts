module app_launcher {

    export class AppLauncher {

        private mainContainer:api_dom.DivEl;
        private adminApplicationFrames:api_dom.DivEl;
        private appIframes:{[name: string]: api_dom.IFrameEl;};
        private lostConnectionDetector:app_launcher.LostConnectionDetector;
        private router:AppRouter;

        constructor(mainContainer:api_dom.DivEl) {
            this.mainContainer = mainContainer;
            this.appIframes = {};


            this.adminApplicationFrames = new api_dom.DivEl();
            this.adminApplicationFrames.getEl().setHeight('100%').setWidth('100%');

            var appManager = new api_app.AppManager();
            appManager.addListener({
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

        loadApplication(app:Application) {
            if (!app.getAppUrl()) {
                console.warn('Missing URL for app "' + app.getName() + '". Cannot be opened.');
                return;
            }

            this.mainContainer.hide();
            Applications.getAllApps().forEach((currentApp:Application) => {
                if (currentApp != app) {
                    currentApp.hide();
                }
            });

            if (app.hasAppFrame()) {
                app.getAppFrame().show();
            } else {
                this.adminApplicationFrames.appendChild(app.getAppFrame());
                this.showLoadMask();
                console.log("appframe", app.getAppFrame().getHTMLElement()["contentWindow"]);
                app.getAppFrame().getHTMLElement()["contentWindow"].setRouter(this.router);
            }
        }

        showLauncherScreen() {
            Applications.getAllApps().forEach((app:Application) => {
                app.hide();
            });
            this.mainContainer.show();
            hasher.setHash(AppRouter.HOME_HASH_ID);
        }

        private showLoadMask() {
            // TODO implement loadMask
        }

        setRouter(router:AppRouter) {
            this.router = router;
        }
    }
}
