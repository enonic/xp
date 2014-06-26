module api.app {

    export class AppManager {
        private static _instance: api.app.AppManager = null;

        private connectionLostListeners: {():void}[] = [];

        private connectionRestoredListeners: {():void}[] = [];

        private showLauncherListeners: {():void}[] = [];

        constructor() {
            api.app.AppManager._instance = this;

        }

        onConnectionLost(listener: ()=>void) {
            this.connectionLostListeners.push(listener);
        }

        onConnectionRestored(listener: ()=>void) {
            this.connectionRestoredListeners.push(listener);
        }

        unConnectionLost(listener: ()=>void) {
            this.connectionLostListeners = this.connectionLostListeners.filter((currentListener: ()=>void) => {
                return listener != currentListener;
            });
        }

        unConnectionRestored(listener: ()=>void) {
            this.connectionRestoredListeners = this.connectionRestoredListeners.filter((currentListener: ()=>void) => {
                return listener != currentListener;
            });
        }

        notifyConnectionLost() {
            this.connectionLostListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        notifyConnectionRestored() {
            this.connectionRestoredListeners.forEach((listener: ()=>void) => {
                listener.call(this);
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
