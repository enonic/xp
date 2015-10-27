module api.app {

    export interface ServerEventJson {
        type: string;
        event: any;
    }

    export interface Event2Json extends ServerEventJson {
        timestamp: number;
        distributed: boolean;
        data: Event2DataJson;
    }

    export interface Event2DataJson {
        nodes: Event2NodeJson[];
    }

    export interface Event2NodeJson {
        id: string;
        path: string;
        newPath: string;
    }

    export class ServerEventsConnection {
        private static KEEP_ALIVE_TIME: number = 30 * 1000;

        private ws: WebSocket;
        private reconnectInterval: number;
        private serverEventReceivedListeners: {(event: api.event.Event):void}[] = [];
        private connectionLostListeners: {():void}[] = [];
        private connectionRestoredListeners: {():void}[] = [];
        private connected: boolean = false;
        private disconnectTimeoutHandle: number;
        private keepConnected: boolean = false;
        private downTime: number;
        private keepAliveIntervalId: number;

        public static debug: boolean = false;

        constructor(reconnectIntervalSeconds: number = 5) {
            this.ws = null;
            this.reconnectInterval = reconnectIntervalSeconds * 1000;
        }

        public connect() {
            if (!WebSocket) {
                console.warn('ServerEventsConnection: WebSockets not supported. Server events disabled.');
                return;
            }
            var wsUrl = api.util.UriHelper.joinPath(this.getWebSocketUriPrefix(), 'admin', 'event');
            this.keepConnected = true;
            this.doConnect(wsUrl);
        }

        private doConnect(wsUrl: string) {
            this.ws = new WebSocket(wsUrl, 'text');

            this.ws.addEventListener('close', (ev: CloseEvent) => {
                clearInterval(this.keepAliveIntervalId);
                if (ServerEventsConnection.debug) {
                    var m = 'ServerEventsConnection: connection closed to ' + wsUrl;
                    if (this.downTime > 0) {
                        m += '\nUptime: ' + (new Date().getTime() - this.downTime);
                    }
                    console.warn(m);
                    this.downTime = new Date().getTime();
                }
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
                if (ServerEventsConnection.debug) {
                    console.error('ServerEventsConnection: Unable to connect to server web socket on ' + wsUrl, ev);
                }
            });

            this.ws.addEventListener('message', (remoteEvent: any) => {
                var jsonEvent = <ServerEventJson> JSON.parse(remoteEvent.data);
                if (ServerEventsConnection.debug) {
                    console.debug('ServerEventsConnection: Server event [' + jsonEvent.type + ']', jsonEvent.event);
                }
                this.handleServerEvent(jsonEvent);
            });

            this.ws.addEventListener('open', (event: Event) => {
                if (ServerEventsConnection.debug) {
                    var m = 'ServerEventsConnection: connection opened to ' + wsUrl;
                    if (this.downTime > 0) {
                        m += '\nDowntime: ' + (new Date().getTime() - this.downTime);
                    }
                    console.log(m);
                    this.downTime = new Date().getTime();
                }
                clearTimeout(this.disconnectTimeoutHandle);
                this.keepAliveIntervalId = setInterval(() => {
                    if (this.connected) {
                        this.ws.send("KeepAlive");
                        if (ServerEventsConnection.debug) {
                            console.log('ServerEventsConnection: Sending Keep Alive message');
                        }
                    }
                }, ServerEventsConnection.KEEP_ALIVE_TIME);
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
            console.log("Event " + eventType + " received");
            if (eventType === 'ApplicationEvent') {
                return api.application.ApplicationEvent.fromJson(serverEventJson.event);
            }
            if (eventType.indexOf('node.') === 0) {
                var event = api.content.ContentServerEvent.fromEvent2Json(<Event2Json>serverEventJson);
                return event;
            }
            if (eventType === 'ContentChangeEvent') {
                var event = api.content.ContentServerEvent.fromJson(serverEventJson.event);
                return null;
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
