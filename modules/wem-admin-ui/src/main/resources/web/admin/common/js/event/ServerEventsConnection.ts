module api.event {

    interface ServerEventJson {
        type: string;
        event: any;
    }

    export class ServerEventsConnection {

        private ws: WebSocket;

        constructor() {
            this.ws = null;
        }

        public connect() {
            var wsUrl = this.getWebSocketUriPrefix() + api.util.getAdminUri('event');

            this.ws = new WebSocket(wsUrl, 'text');

            this.ws.addEventListener('close', (ev: CloseEvent) => {
                console.info('WebSocket connection to server closed', ev);
                // TODO attempt to reconnect?
            });

            this.ws.addEventListener('error', (ev: ErrorEvent) => {
                console.error('Unable to connect to server web socket on ' + wsUrl, ev);
                // TODO attempt to reconnect?
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
                clientEvent.fire();
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
