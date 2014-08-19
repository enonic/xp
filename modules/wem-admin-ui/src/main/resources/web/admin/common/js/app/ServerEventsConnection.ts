module api.app {

    interface ServerEventJson {
        type: string;
        event: any;
    }

    export class ServerEventsConnection {

        private ws: WebSocket;
        private reconnectInterval: number;
        private serverEventReceivedListeners: {(event: api.event.Event):void}[] = [];

        constructor(reconnectIntervalSeconds: number = 10) {
            this.ws = null;
            this.reconnectInterval = reconnectIntervalSeconds * 1000;
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
            var clientEvent: api.event.Event = this.translateServerEvent(serverEventJson);

            if (clientEvent) {
                this.notifyServerEvent(clientEvent);
            }
        }

        private translateServerEvent(serverEventJson: ServerEventJson): api.event.Event {
            var eventType = serverEventJson.type;
            if (eventType === 'ModuleUpdatedEvent') {
                return api.module.ModuleUpdatedEvent.fromJson(serverEventJson.event);
            }
            else if (eventType === 'ContentTypeUpdatedEvent') {
                return api.schema.content.ContentTypeUpdatedEvent.fromJson(serverEventJson.event);
            }
            return null;
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

        private notifyServerEvent(serverEvent: api.event.Event) {
            this.serverEventReceivedListeners.forEach((listener: (event: api.event.Event)=>void)=> {
                listener.call(this, serverEvent);
            });
        }

        onServerEvent(listener: (event: api.event.Event) => void) {
            this.serverEventReceivedListeners.push(listener);
        }

        unServerEvent(listener: (event: api.event.Event) => void) {
            this.serverEventReceivedListeners =
            this.serverEventReceivedListeners.filter((currentListener: (event: api.event.Event)=>void)=> {
                return currentListener != listener;
            });
        }
    }

}
