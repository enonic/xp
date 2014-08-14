module api.event {

    interface ServerEventJson {
        type: string;
        event: any;
    }

    export class ServerEventsConnection {

        private ws: WebSocket;
        private reconnectInterval: number;
        private applications: api.app.Application[];

        constructor(applications: api.app.Application[], reconnectIntervalSeconds: number = 10) {
            this.ws = null;
            this.reconnectInterval = reconnectIntervalSeconds * 1000;
            this.applications = applications;
        }

        public connect() {
            if (!WebSocket) {
                console.warn('WebSockets not supported. Server events disabled.');
                return;
            }
            var wsUrl = this.getWebSocketUriPrefix() + api.util.getAdminUri('event');

            this.ws = new WebSocket(wsUrl, 'text');

            this.ws.addEventListener('close', (ev: CloseEvent) => {
                // attempt to reconnect
                setTimeout(()=> {
                    this.connect();
                }, this.reconnectInterval);
            });

            this.ws.addEventListener('error', (ev: ErrorEvent) => {
                // console.log('Unable to connect to server web socket on ' + wsUrl, ev);
            });

            this.ws.addEventListener('message', (remoteEvent: any) => {
                var jsonEvent = <ServerEventJson> JSON.parse(remoteEvent.data);
                console.log('Server event [' + jsonEvent.type + ']', jsonEvent.event);
                this.handleServerEvent(jsonEvent);
            });
        }

        private handleServerEvent(serverEventJson: ServerEventJson): void {
            var eventType = serverEventJson.type;
            var clientEvent: api.event.Event = null;
            if (eventType === 'ModuleUpdatedEvent') {
                clientEvent = api.module.ModuleUpdatedEvent.fromJson(serverEventJson.event);
            }

            if (clientEvent) {
                this.applications.forEach((app: api.app.Application)=> {
                    var appWindow = app.getWindow();
                    if (appWindow) {
                        clientEvent.fire(appWindow);
                    }
                });
            }
        }

        private getWebSocketUriPrefix(): string {
            var loc = window.location, newUri;
            if (loc.protocol === "https:") {
                newUri = "wss:";
            } else {
                newUri = "ws:";
            }
            newUri += "//" + loc.host;
            return newUri;
        }

    }

}
