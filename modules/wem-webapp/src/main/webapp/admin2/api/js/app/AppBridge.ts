module api_app {

    export class AppBridge implements api_event.Observable {
        private static _instance:AppBridge = null;
        private showLauncherHandler:()=>void;

        private listeners:AppBridgeListener[] = [];

        constructor() {
            AppBridge._instance = this;
        }

        showLauncher():void {
            this.notifyShowLauncher();
        }

        addListener(listener:AppBridgeListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:AppBridgeListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        onConnectionLost() {
            this.notifyConnectionLost();
        }

        onConnectionRestored() {
            this.notifyConnectionRestored();
        }

        private notifyConnectionLost() {
            this.listeners.forEach((listener:AppBridgeListener) => {
                if (listener.onConnectionLost) {
                    listener.onConnectionLost();
                }
            });
        }

        private notifyConnectionRestored() {
            this.listeners.forEach((listener:AppBridgeListener) => {
                if (listener.onConnectionRestored) {
                    listener.onConnectionRestored();
                }
            });
        }

        private notifyShowLauncher() {
            this.listeners.forEach((listener:AppBridgeListener) => {
                if (listener.onShowLauncher) {
                    listener.onShowLauncher();
                }
            });
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
