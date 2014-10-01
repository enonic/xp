module app.launcher {

    export class LostConnectionDetector {

        private intervalId: number = -1;
        private pollIntervalMs: number;

        private connected: boolean = true;

        private connectionLostListeners: {():void}[] = [];

        private connectionRestoredListeners: {():void}[] = [];

        constructor(pollIntervalMs: number = 5000) {
            this.pollIntervalMs = pollIntervalMs;

            var managerInstance = api.app.AppManager.instance();

            this.onConnectionLost(() => {
                managerInstance.notifyConnectionLost();
            });

            this.onConnectionRestored(() => {
                managerInstance.notifyConnectionRestored();
            });
        }

        startPolling() {

            this.stopPolling();
            this.doPoll();
            this.intervalId = setInterval(() => {
                this.doPoll();
            }, this.pollIntervalMs);
        }

        stopPolling() {
            clearInterval(this.intervalId);
        }

        doPoll() {
            var xhr = new XMLHttpRequest();
            xhr.open('GET', api.util.UriHelper.getRestUri('status'));
            xhr.timeout = this.pollIntervalMs;
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        if (!this.connected) {
                            this.notifyConnectionRestored();
                            this.connected = !this.connected;
                        }
                    } else {
                        if (this.connected) {
                            this.notifyConnectionLost();
                            this.connected = !this.connected;
                        }
                    }
                }
            };
            xhr.send(null);
        }

        onConnectionLost(listener: ()=>void) {
            this.connectionLostListeners.push(listener);
        }

        onConnectionRestored(listener: ()=>void) {
            this.connectionRestoredListeners.push(listener);
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
    }
}