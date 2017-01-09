module api.app {

    export class AppManager {
        private static INSTANCE: api.app.AppManager = null;

        private connectionLostListeners: {():void}[] = [];

        private connectionRestoredListeners: {():void}[] = [];

        private showLauncherListeners: {():void}[] = [];

        constructor() {
            api.app.AppManager.INSTANCE = this;

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
            if (api.app.AppManager.INSTANCE) {
                return api.app.AppManager.INSTANCE;
            } else if (window !== window.parent) {
                // look for instance in parent frame
                let apiAppModule = (<any> window.parent).api.app;
                if (apiAppModule && apiAppModule.AppManager) {
                    let parentAppManager = <api.app.AppManager> apiAppModule.AppManager.INSTANCE;
                    if (parentAppManager) {
                        api.app.AppManager.INSTANCE = parentAppManager;
                    }
                }
            }
            return api.app.AppManager.INSTANCE;
        }
    }
}
