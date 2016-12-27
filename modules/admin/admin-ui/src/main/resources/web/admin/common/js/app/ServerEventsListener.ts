module api.app {

    import ContentServerEvent = api.content.event.ContentServerEvent;
    import BatchContentServerEvent = api.content.event.BatchContentServerEvent;

    export class ServerEventsListener {

        private serverEventsConnection: api.app.ServerEventsConnection;
        private applications: api.app.Application[];
        private aggregator: ServerEventAggregator;

        constructor(applications: api.app.Application[]) {
            this.applications = applications;
            this.serverEventsConnection = api.app.ServerEventsConnection.getInstance();
            this.serverEventsConnection.onServerEvent((event: api.event.Event) => this.onServerEvent(event));
            this.aggregator = new ServerEventAggregator();

            this.aggregator.onBatchIsReady(() => {

                var event = new BatchContentServerEvent(<ContentServerEvent[]>this.aggregator.getEvents(), this.aggregator.getType());
                this.fireEvent(event);

                this.aggregator.resetEvents();
            });
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
            if (!api.ObjectHelper.iFrameSafeInstanceOf(event, ContentServerEvent)) {
                this.fireEvent(event);
            } else {
                this.aggregator.appendEvent(<ContentServerEvent>event);
            }
        }

        private fireEvent(event: api.event.Event) {
            this.applications.forEach((application)=> {
                var appWindow = application.getWindow();
                if (appWindow) {
                    event.fire(appWindow);
                }
            });
        }


    }

}
