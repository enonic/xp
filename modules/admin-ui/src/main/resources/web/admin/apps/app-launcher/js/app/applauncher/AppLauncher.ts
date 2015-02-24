module app.launcher {

    export class AppLauncher {

        private appManager:api.app.AppManager;

        private homeMainContainer:app.home.HomeMainContainer;

        private adminApplicationFrames:api.dom.DivEl;

        private loadMask:api.ui.mask.LoadMask;

        private currentApplication:api.app.Application;

        private currentApplicationHash:string;

        private allowedApplications:{[id:string]:api.app.Application};

        constructor(mainContainer:app.home.HomeMainContainer) {
            this.homeMainContainer = mainContainer;
            this.allowedApplications = {};

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

            api.app.ShowAppLauncherEvent.on((event) => {
                api.ui.KeyBindings.get().shelveBindings();

                this.currentApplication = event.getApplication();
                this.currentApplicationHash = hasher.getHash();
                this.homeMainContainer.enableReturnButton();
                this.homeMainContainer.disableBranding();
                this.homeMainContainer.giveFocus();

                Applications.getAllApps().forEach((app:api.app.Application) => {
                    if (app != event.getApplication()) {
                        app.hide();
                    }
                });

                this.showLauncherScreen();

                if (event.isSessionExpired()) {
                    new app.home.LogOutEvent().fire();
                }
            });
        }

        showApplication(application: api.app.Application): boolean {
            if(application.isNotDisplayed()) {
                this.doShowApplication(application).then((result:boolean) => {
                    return result;
                }).catch((reason:any) => {
                    application.setDisplayingStatus(api.app.ApplicationShowStatus.NOT_DISPLAYED);
                }).done();
            } else {
                return true;
            }
        }

        private doShowApplication(application:api.app.Application): wemQ.Promise<boolean> {
            var deferred = wemQ.defer<boolean>();

            application.setDisplayingStatus(api.app.ApplicationShowStatus.PREPARING);

            if (!this.isAllowedApp(application)) {

                new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                    var allowedApps = app.launcher.Applications.getAppsByIds(loginResult.getApplications());
                    this.setAllowedApps(allowedApps);

                    if (loginResult.isAuthenticated() && this.isAllowedApp(application)) {
                        return this.doLoadApplication(application);

                    } else {
                        this.showLauncherScreen();
                        deferred.resolve(false);
                    }

                }).catch((reason: any) => {
                    this.showLauncherScreen();
                    deferred.resolve(false);
                }).done();

            } else {
                return this.doLoadApplication(application);
            }

            return deferred.promise;
        }

        private doLoadApplication(application:api.app.Application):wemQ.Promise<boolean> {
            var deferred = wemQ.defer<boolean>();
            Applications.getAllApps().forEach((app:api.app.Application) => {
                if (app == application) {
                    app.show();
                } else {
                    app.hide();
                }
            });

            if (!application.getAppUrl()) {
                console.warn('Missing URL for app "' + application.getName() + '". Cannot be opened.');
                deferred.resolve(false);
            }

            api.ui.KeyBindings.get().reset();
            this.homeMainContainer.hide();

            var appFrame:api.dom.IFrameEl = application.getAppFrame();
            if (application.isLoaded()) {
                appFrame.show();
                appFrame.giveFocus();
                deferred.resolve(true);
            } else {
                this.adminApplicationFrames.appendChild(appFrame);
                this.loadMask.show();
                application.onLoaded(() => {
                    this.loadMask.hide();
                    appFrame.giveFocus();
                    deferred.resolve(true);
                });
            }
            var type = api.app.AppLauncherEventType.Show;
            appFrame.postMessage(<api.app.AppLauncherEvent>{appLauncherEvent: api.app.AppLauncherEventType[type]});

            return deferred.promise;
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

        setAllowedApps(applications:api.app.Application[]) {
            this.allowedApplications = {};
            applications.forEach((application:api.app.Application) => {
                this.allowedApplications[application.getId()] = application;
            });
        }

        private isAllowedApp(application:api.app.Application):boolean {
            return !!this.allowedApplications[application.getId()];
        }

        private returnToApp() {
            this.homeMainContainer.hide();
            api.ui.KeyBindings.get().unshelveBindings();
            this.currentApplication.getAppFrame().giveFocus();
            hasher.setHash(this.currentApplicationHash);
        }

    }
}
