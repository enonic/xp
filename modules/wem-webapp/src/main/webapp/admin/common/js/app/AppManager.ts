module api.app {

    export class AppManager implements api.event.Observable {
        private static _instance: api.app.AppManager = null;

        private listeners: api.app.AppManagerListener[] = [];

        constructor() {
            api.app.AppManager._instance = this;
        }

        showLauncher(): void {
            this.notifyShowLauncher();
        }

        addListener(listener: api.app.AppManagerListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: api.app.AppManagerListener) {
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
            this.listeners.forEach((listener: api.app.AppManagerListener) => {
                listener.onConnectionLost();
            });
        }

        private notifyConnectionRestored() {
            this.listeners.forEach((listener: api.app.AppManagerListener) => {
                listener.onConnectionRestored();
            });
        }

        private notifyShowLauncher() {
            this.listeners.forEach((listener: api.app.AppManagerListener) => {
                listener.onShowLauncher();
            });
        }

        static instance(): api.app.AppManager {
            if (api.app.AppManager._instance) {
                return api.app.AppManager._instance;
            } else if (window !== window.parent) {
                // look for instance in parent frame
                var apiAppModule = (<any> window.parent).api.app;
                if (apiAppModule && apiAppModule.AppManager) {
                    var parentAppManager = <api.app.AppManager> apiAppModule.AppManager._instance;
                    if (parentAppManager) {
                        api.app.AppManager._instance = parentAppManager;
                    }
                }
            }
            return api.app.AppManager._instance;
        }
    }
}
