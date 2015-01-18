module api.app {

    interface ServerEventJson {
        type: string;
        event: any;
    }

    export class ServerEventsConnection {

        private ws: WebSocket;
        private reconnectInterval: number;
        private serverEventReceivedListeners: {(event: api.event.Event):void}[] = [];
        private connectionLostListeners: {():void}[] = [];
        private connectionRestoredListeners: {():void}[] = [];
        private connected: boolean = false;
        private disconnectTimeoutHandle: number;
        private keepConnected: boolean = false;

        constructor(reconnectIntervalSeconds: number = 5) {
            this.ws = null;
            this.reconnectInterval = reconnectIntervalSeconds * 1000;
        }

        public connect() {
            if (!WebSocket) {
                console.warn('WebSockets not supported. Server events disabled.');
                return;
            }
            var wsUrl = api.util.UriHelper.joinPath(this.getWebSocketUriPrefix(), 'admin', 'event');
            this.keepConnected = true;
            this.doConnect(wsUrl);
        }

        private doConnect(wsUrl: string) {
            this.ws = new WebSocket(wsUrl, 'text');

            this.ws.addEventListener('close', (ev: CloseEvent) => {
                this.disconnectTimeoutHandle = setTimeout(() => {
                    if (this.connected) {
                        if (this.keepConnected) {
                            this.notifyConnectionLost();
                        }
                        this.connected = !this.connected;
                    }
                }, this.reconnectInterval + 1000);

                // attempt to reconnect
                if (this.keepConnected) {
                    setTimeout(()=> {
                        if (this.keepConnected) {
                            this.doConnect(wsUrl);
                        }
                    }, this.reconnectInterval);
                }
            });

            this.ws.addEventListener('error', (ev: ErrorEvent) => {
                // console.log('Unable to connect to server web socket on ' + wsUrl, ev);
            });

            this.ws.addEventListener('message', (remoteEvent: any) => {
                var jsonEvent = <ServerEventJson> JSON.parse(remoteEvent.data);
                //console.log('Server event [' + jsonEvent.type + ']', jsonEvent.event);
                this.handleServerEvent(jsonEvent);
            });

            this.ws.addEventListener('open', (event: Event) => {
                clearInterval(this.disconnectTimeoutHandle);
                if (!this.connected) {
                    this.notifyConnectionRestored();
                    this.connected = !this.connected;
                }
            });
        }

        public disconnect() {
            this.keepConnected = false;
            if (this.ws) {
                this.ws.close();
            }
        }

        public isConnected(): boolean {
            return this.ws.readyState === WebSocket.OPEN;
        }

        private handleServerEvent(serverEventJson: ServerEventJson): void {
            var clientEvent: api.event.Event = this.translateServerEvent(serverEventJson);

            if (clientEvent) {
                this.notifyServerEvent(clientEvent);
            }
        }

        private translateServerEvent(serverEventJson: ServerEventJson): api.event.Event {
            var eventType = serverEventJson.type;
            //if (eventType === 'ContentCreatedEvent') {
            //    return api.content.ContentCreatedEvent.fromJson(serverEventJson.event);
            //}
            if (eventType === 'ModuleUpdatedEvent') {
                return api.module.ModuleUpdatedEvent.fromJson(serverEventJson.event);
            }
            //if (eventType === 'ContentUpdatedEvent') {
            //    return api.content.ContentUpdatedEvent.fromJson(serverEventJson.event);
            //}
            if (eventType === 'ContentPublishedEvent') {
                return api.content.ContentPublishedEvent.fromJson(serverEventJson.event);
            }
            else if (eventType === 'ContentTypeUpdatedEvent') {
                return api.schema.content.ContentTypeUpdatedEvent.fromJson(serverEventJson.event);
            }
            else if (eventType === 'ContentTypeDeletedEvent') {
                return api.schema.content.ContentTypeDeletedEvent.fromJson(serverEventJson.event);
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

        private notifyConnectionLost() {
            this.connectionLostListeners.forEach((listener: (event)=>void)=> {
                listener.call(this);
            });
        }

        onConnectionLost(listener: () => void) {
            this.connectionLostListeners.push(listener);
        }

        unConnectionLost(listener: () => void) {
            this.connectionLostListeners =
            this.connectionLostListeners.filter((currentListener: ()=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyConnectionRestored() {
            this.connectionRestoredListeners.forEach((listener: (event)=>void)=> {
                listener.call(this);
            });
        }

        onConnectionRestored(listener: () => void) {
            this.connectionRestoredListeners.push(listener);
        }

        unConnectionRestored(listener: () => void) {
            this.connectionRestoredListeners =
            this.connectionRestoredListeners.filter((currentListener: ()=>void)=> {
                return currentListener != listener;
            });
        }

    }

}
