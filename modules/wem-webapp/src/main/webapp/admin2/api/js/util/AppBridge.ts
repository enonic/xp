module api_util {

    var _instance:api_util.AppBridge = null;

    export class AppBridge {
        private showLauncherHandler:()=>void;

        constructor() {
            _instance = this;
        }

        showLauncher():void {
            if (this.showLauncherHandler) {
                this.showLauncherHandler();
            }
        }

        onShowLauncher(handler:()=>void) {
            this.showLauncherHandler = handler;
        }

        static instance():api_util.AppBridge {
            if (_instance) {
                return _instance;
            } else if (window !== window.parent) {
                // look for instance in parent frame
                var appUtilModule = (<any> window.parent).api_util;
                if (appUtilModule && appUtilModule.AppBridge) {
                    var parentAppBridge = <api_util.AppBridge> appUtilModule.AppBridge.instance();
                    if (parentAppBridge) {
                        _instance = parentAppBridge;
                    }
                }
            }
            return _instance;
        }
    }
}
