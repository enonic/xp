module app.launcher {

    export class LostConnectionDetector {

        private intervalId: number = -1;
        private pollIntervalMs: number;

        private connected: boolean = true;
        private authenticated: boolean = false;

        private connectionLostListeners: {():void}[] = [];

        private connectionRestoredListeners: {():void}[] = [];

        private sessionExpiredListeners: {():void}[] = [];

        constructor(pollIntervalMs: number = 15000) {
            this.pollIntervalMs = pollIntervalMs;
        }

        startPolling() {
            this.stopPolling();
            this.intervalId = setInterval(this.doPoll.bind(this), this.pollIntervalMs);
        }

        stopPolling() {
            clearInterval(this.intervalId);
        }

        setAuthenticated(isAuthenticated: boolean) {
            this.authenticated = isAuthenticated;
        }

        private doPoll() {
            var request = new api.system.StatusRequest();
            request.setTimeout(this.pollIntervalMs);
            request.sendAndParse().then((status: api.system.StatusResult) => {
                if (!this.connected) {
                    this.notifyConnectionRestored();
                    this.connected = !this.connected;
                }
                if (this.authenticated && !status.isAuthenticated()) {
                    this.notifySessionExpired();
                }
                this.authenticated = status.isAuthenticated();
            }).catch((reason: any) => {
                if (this.connected) {
                    this.notifyConnectionLost();
                    this.connected = !this.connected;
                }
            }).done();
        }

        onConnectionLost(listener: ()=>void) {
            this.connectionLostListeners.push(listener);
        }

        onConnectionRestored(listener: ()=>void) {
            this.connectionRestoredListeners.push(listener);
        }

        onSessionExpired(listener: ()=>void) {
            this.sessionExpiredListeners.push(listener);
        }

        unConnectionLost(listener: ()=>void) {
            this.connectionLostListeners = this.connectionLostListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        unConnectionRestored(listener: ()=>void) {
            this.connectionRestoredListeners = this.connectionRestoredListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            })
        }

        unSessionExpired(listener: ()=>void) {
            this.sessionExpiredListeners = this.sessionExpiredListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        private notifyConnectionLost() {
            this.connectionLostListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyConnectionRestored() {
            this.connectionRestoredListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifySessionExpired() {
            this.sessionExpiredListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }
}