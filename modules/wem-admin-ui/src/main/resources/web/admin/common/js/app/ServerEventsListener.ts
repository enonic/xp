module api.app {

    export class ServerEventsListener {

        private serverEventsConnection: api.app.ServerEventsConnection;
        private applications: api.app.Application[];

        constructor(applications: api.app.Application[]) {
            this.applications = applications;
            this.serverEventsConnection = new api.app.ServerEventsConnection();
            this.serverEventsConnection.onServerEvent((event: api.event.Event) => this.onServerEvent(event));
            this.serverEventsConnection.connect();
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
