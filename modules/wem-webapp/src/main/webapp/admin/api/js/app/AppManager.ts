module api_app {

    export class AppManager implements api_event.Observable {
        private static _instance:api_app.AppManager = null;

        private listeners:api_app.AppManagerListener[] = [];

        constructor() {
            api_app.AppManager._instance = this;
        }

        showLauncher():void {
            this.notifyShowLauncher();
        }

        addListener(listener:api_app.AppManagerListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:api_app.AppManagerListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr !== listener;
            });
        }

        onConnectionLost() {
            this.notifyConnectionLost();
        }

        onConnectionRestored() {
            this.notifyConnectionRestored();
        }

        private notifyConnectionLost() {
            this.listeners.forEach((listener:api_app.AppManagerListener) => {
                if (listener.onConnectionLost) {
                    listener.onConnectionLost();
                }
            });
        }

        private notifyConnectionRestored() {
            this.listeners.forEach((listener:api_app.AppManagerListener) => {
                if (listener.onConnectionRestored) {
                    listener.onConnectionRestored();
                }
            });
        }

        private notifyShowLauncher() {
            this.listeners.forEach((listener:api_app.AppManagerListener) => {
                if (listener.onShowLauncher) {
                    listener.onShowLauncher();
                }
            });
        }

        static instance():api_app.AppManager {
            if (api_app.AppManager._instance) {
                return api_app.AppManager._instance;
            } else if (window !== window.parent) {
                // look for instance in parent frame
                var apiAppModule = (<any> window.parent).api_app;
                if (apiAppModule && apiAppModule.AppManager) {
                    var parentAppManager = <api_app.AppManager> apiAppModule.AppManager._instance;
                    if (parentAppManager) {
                        api_app.AppManager._instance = parentAppManager;
                    }
                }
            }
           return api_app.AppManager._instance;
        }
    }
}
