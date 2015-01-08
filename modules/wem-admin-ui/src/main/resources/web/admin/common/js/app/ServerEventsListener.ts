module api.app {

    export class ServerEventsListener {

        private serverEventsConnection: api.app.ServerEventsConnection;
        private applications: api.app.Application[];

        constructor(applications: api.app.Application[]) {
            this.applications = applications;
            this.serverEventsConnection = new api.app.ServerEventsConnection();
            this.serverEventsConnection.onServerEvent((event: api.event.Event) => this.onServerEvent(event));
        }

        start() {
            this.serverEventsConnection.connect();
        }

        stop() {
            this.serverEventsConnection.disconnect();
        }

        onConnectionLost(listener: () => void) {
            this.serverEventsConnection.onConnectionLost(listener);
        }

        unConnectionLost(listener: () => void) {
            this.serverEventsConnection.unConnectionLost(listener);
        }

        onConnectionRestored(listener: () => void) {
            this.serverEventsConnection.onConnectionRestored(listener);
        }

        unConnectionRestored(listener: () => void) {
            this.serverEventsConnection.unConnectionRestored(listener);
        }

        private onServerEvent(event: api.event.Event) {
            this.applications.forEach((application)=> {
                var appWindow = application.getWindow();
                if (appWindow) {
                    event.fire(appWindow);
                }
            });
        }

    }

}
