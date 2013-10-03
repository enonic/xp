module app_launcher {

    export class LostConnectionDetector implements api_event.Observable {

        private intervalId:number = -1;
        private pollIntervalMs:number;

        private connected:boolean = true;

        private listeners:LostConnectionDetectorListener[] = [];

        constructor(pollIntervalMs:number = 5000000) {
            this.pollIntervalMs = pollIntervalMs;

            this.addListener(api_app.AppManager.instance());
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
            xhr.open('GET', api_util.getAbsoluteUri('admin/rest/status'));
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

        addListener(listener:LostConnectionDetectorListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:LostConnectionDetectorListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr !== listener;
            });
        }

        private notifyConnectionLost() {
            this.listeners.forEach((listener:LostConnectionDetectorListener) => {
                if (listener.onConnectionLost) {
                    listener.onConnectionLost();
                }
            });
        }

        private notifyConnectionRestored() {
            this.listeners.forEach((listener:LostConnectionDetectorListener) => {
                if (listener.onConnectionRestored) {
                    listener.onConnectionRestored();
                }
            });
        }
    }
}