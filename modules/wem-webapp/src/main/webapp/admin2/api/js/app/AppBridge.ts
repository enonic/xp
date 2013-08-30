module api_app {

    export class AppBridge {
        private static _instance:AppBridge = null;
        private showLauncherHandler:()=>void;

        constructor() {
            AppBridge._instance = this;
        }

        showLauncher():void {
            if (this.showLauncherHandler) {
                this.showLauncherHandler();
            }
        }

        onShowLauncher(handler:()=>void) {
            this.showLauncherHandler = handler;
        }

        static instance():AppBridge {
            if (AppBridge._instance) {
                return AppBridge._instance;
            } else if (window !== window.parent) {
                // look for instance in parent frame

                var apiAppModule = (<any> window.parent).api_app;
                if (apiAppModule && apiAppModule.AppBridge) {
                    var parentAppBridge = <AppBridge> apiAppModule.AppBridge.instance();
                    if (parentAppBridge) {
                        AppBridge._instance = parentAppBridge;
                    }
                }
            }
            return AppBridge._instance;
        }
    }
}
