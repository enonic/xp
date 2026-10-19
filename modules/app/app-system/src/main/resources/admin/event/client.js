/**
 * Client for the admin events hub, served by the hub itself at `<admin:events>/client.js`.
 *
 * A page imports it and calls `connect`, which subscribes through a shared worker started from
 * this same url, or over a socket of the page's own where shared workers are unavailable. The url
 * is what makes a shared worker shared, so an admin tool that loads this one shares a socket with
 * every other admin page of the browser, while a copy of its own would not.
 *
 *     const {connect} = await import(`${eventsUrl}/client.js`);
 *     connect({onEvent: e => ..., onLoss: l => ..., onConnectionChange: c => ..., onSessionEnd: () => ...})
 *         .subscribe(topic);
 *
 * `onLoss` reports how many messages were missed, or null when that cannot be known. The topics a
 * connection did not subscribe to are not delivered to it, whatever else the worker carries.
 */

const WS_PROTOCOL = 'json';

// a shared worker is shared by script url and name, so both are part of what admin tools agree on
const WORKER_NAME = 'xp-admin-events-socket-v2';

const CONNECTION_TIMEOUT = 60000;
const PING_INTERVAL = 50000;
const PONG_TIMEOUT = 15000;
const MAX_RECONNECT_DELAY = 30000;
const MAX_SUBSCRIBE_RETRY_DELAY = 30000;

const SESSION_END_CLOSE_CODE = 1008;

/**
 * Holds the wanted-subscription set and resubscribes it on every reconnect. A subscription denied
 * as `unknown` is retried with backoff; `forbidden` ends retries until the next reconnect. Loss is
 * reported per subscription: a sequence gap within an epoch is countable, an epoch change is not.
 */
export class AdminEventsSocket {

    constructor(url, handlers, probeUrl) {
        this.url = url;
        this.handlers = handlers;
        this.probeUrl = probeUrl;
        this.subscriptions = new Map();
        this.ws = null;
        this.reconnectAttempts = 0;
        this.closed = false;
        this.sessionEnded = false;
        this.connected = null;
    }

    subscribe(topic) {
        if (this.subscriptions.has(topic)) {
            return;
        }
        this.subscriptions.set(topic, {
            acked: false,
            everAcked: false,
            seq: 0,
            epoch: null,
            retryAttempts: 0,
            retryTimeout: undefined,
        });
        this.send({type: 'subscribe', topic});
    }

    unsubscribe(topic) {
        const subscription = this.subscriptions.get(topic);
        if (!subscription) {
            return;
        }
        clearTimeout(subscription.retryTimeout);
        this.subscriptions.delete(topic);
        this.send({type: 'unsubscribe', topic});
    }

    publish(topic, data) {
        this.send({type: 'pub', topic, data});
    }

    connect() {
        this.closed = false;
        this.sessionEnded = false;

        if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
            return;
        }

        const ws = new WebSocket(this.url, [WS_PROTOCOL]);
        this.ws = ws;
        let opened = false;

        this.connectionTimeout = setTimeout(() => {
            if (ws.readyState === WebSocket.CONNECTING) {
                ws.close();
            }
        }, CONNECTION_TIMEOUT);

        ws.onopen = () => {
            opened = true;
            clearTimeout(this.connectionTimeout);
            this.reconnectAttempts = 0;
            this.setConnected(true);
            this.resubscribeAll();

            this.pingInterval = setInterval(() => {
                this.send({type: 'ping'});
                clearTimeout(this.pongTimeout);
                this.pongTimeout = setTimeout(() => this.reconnect(), PONG_TIMEOUT);
            }, PING_INTERVAL);
        };

        ws.onmessage = e => this.handleMessage(e);

        ws.onclose = event => {
            clearTimeout(this.connectionTimeout);
            this.teardownTimers();
            if (this.closed) {
                return;
            }
            this.setConnected(false);
            if (event.code === SESSION_END_CLOSE_CODE) {
                this.endSession();
                return;
            }
            if (!opened && this.probeUrl) {
                this.probeSession();
                return;
            }
            this.scheduleReconnect();
        };

        ws.onerror = () => {
            return;
        };
    }

    close() {
        this.closed = true;
        this.teardownTimers();
        clearTimeout(this.reconnectTimeout);

        if (this.ws) {
            this.ws.onclose = null;
            this.ws.close();
            this.ws = null;
        }
    }

    resubscribeAll() {
        this.subscriptions.forEach((subscription, topic) => {
            clearTimeout(subscription.retryTimeout);
            subscription.retryTimeout = undefined;
            subscription.retryAttempts = 0;
            subscription.acked = false;
            this.send({type: 'subscribe', topic});
        });
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        }
    }

    handleMessage(event) {
        let message;
        try {
            message = JSON.parse(event.data);
        } catch (e) {
            return;
        }

        if (!message || typeof message.type !== 'string') {
            return;
        }

        switch (message.type) {
        case 'ack':
            this.handleAck(message);
            break;
        case 'deny':
            this.handleDeny(message);
            break;
        case 'event':
            this.handleEvent(message);
            break;
        case 'pong':
            clearTimeout(this.pongTimeout);
            break;
        case 'error':
            console.warn('[admin-events] Server reported:', message);
            break;
        default:
            break;
        }
    }

    handleAck(message) {
        const topic = String(message.topic);
        const subscription = this.subscriptions.get(topic);
        if (!subscription) {
            return;
        }

        const seq = Number(message.seq) || 0;
        const epoch = typeof message.epoch === 'string' ? message.epoch : null;

        if (subscription.everAcked) {
            if (epoch !== subscription.epoch) {
                this.handlers.onLoss({topic, count: null});
            } else if (seq > subscription.seq) {
                this.handlers.onLoss({topic, count: seq - subscription.seq});
            }
        }

        subscription.acked = true;
        subscription.everAcked = true;
        subscription.seq = seq;
        subscription.epoch = epoch;
        subscription.retryAttempts = 0;
    }

    handleDeny(message) {
        const topic = String(message.topic);
        const subscription = this.subscriptions.get(topic);
        if (!subscription) {
            return;
        }

        subscription.acked = false;

        if (message.reason === 'unknown') {
            subscription.retryAttempts += 1;
            const delay = Math.min(2 ** subscription.retryAttempts * 1000, MAX_SUBSCRIBE_RETRY_DELAY);
            clearTimeout(subscription.retryTimeout);
            subscription.retryTimeout = setTimeout(() => this.send({type: 'subscribe', topic}), delay);
        } else {
            console.warn(`[admin-events] Subscription to '${topic}' denied:`, message.reason);
        }
    }

    handleEvent(message) {
        const topic = String(message.topic);
        const subscription = this.subscriptions.get(topic);
        if (!subscription || !subscription.acked) {
            return;
        }

        const seq = Number(message.seq) || 0;
        if (seq <= subscription.seq) {
            return;
        }
        if (seq > subscription.seq + 1) {
            this.handlers.onLoss({topic, count: seq - subscription.seq - 1});
        }
        subscription.seq = seq;
        this.handlers.onEvent({topic, data: message.data});
    }

    setConnected(connected) {
        if (connected === this.connected || (this.connected === null && !connected)) {
            return;
        }
        this.connected = connected;
        if (this.handlers.onConnectionChange) {
            this.handlers.onConnectionChange(connected);
        }
    }

    endSession() {
        this.sessionEnded = true;
        if (this.handlers.onSessionEnd) {
            this.handlers.onSessionEnd();
        }
    }

    probeSession() {
        fetch(this.probeUrl, {cache: 'no-store', redirect: 'manual'})
            .then(response => {
                if (this.closed) {
                    return;
                }
                if (response.type === 'opaqueredirect' || response.status === 401 || response.status === 403) {
                    this.endSession();
                } else {
                    this.scheduleReconnect();
                }
            })
            .catch(() => {
                if (!this.closed) {
                    this.scheduleReconnect();
                }
            });
    }

    reconnect() {
        if (this.ws) {
            this.ws.close();
        }
    }

    scheduleReconnect() {
        this.reconnectAttempts += 1;
        const delay = Math.min(2 ** this.reconnectAttempts * 1000, MAX_RECONNECT_DELAY);
        this.reconnectTimeout = setTimeout(() => this.connect(), delay);
    }

    teardownTimers() {
        clearInterval(this.pingInterval);
        clearTimeout(this.pongTimeout);
        this.subscriptions.forEach(subscription => {
            clearTimeout(subscription.retryTimeout);
            subscription.retryTimeout = undefined;
            subscription.acked = false;
        });
    }
}

/**
 * The websocket endpoint, taken from this script's own url: the hub serves the script from the
 * api it upgrades, so whatever path and host reached the script reaches the socket.
 */
export function socketUrl() {
    const href = import.meta.url;
    const base = href.substring(0, href.lastIndexOf('/'));
    return base.replace(/^http/, 'ws');
}

/**
 * Subscribes to topics of the hub, through a shared worker when the browser has them and over a
 * socket of this page's own when it does not.
 *
 * @param handlers `{onEvent, onLoss}`, called only for the topics this connection subscribed to,
 * and optional `onConnectionChange` and `onSessionEnd`
 * @returns `{subscribe(topic)}`
 */
export function connect(handlers) {
    const topics = new Set();

    const deliver = message => {
        if (message.type === 'session-end') {
            if (handlers.onSessionEnd) {
                handlers.onSessionEnd();
            }
            return;
        }
        if (message.type === 'connection') {
            if (handlers.onConnectionChange) {
                handlers.onConnectionChange(message.connected);
            }
            return;
        }
        if (!topics.has(message.topic)) {
            return;
        }
        if (message.type === 'event') {
            handlers.onEvent({topic: message.topic, data: message.data});
        } else if (message.type === 'loss') {
            handlers.onLoss({topic: message.topic, count: message.count});
        }
    };

    let port = null;
    let socket = null;

    const startSocket = () => {
        if (socket) {
            return;
        }
        socket = new AdminEventsSocket(socketUrl(), {
            onEvent: event => deliver({type: 'event', topic: event.topic, data: event.data}),
            onLoss: loss => deliver({type: 'loss', topic: loss.topic, count: loss.count}),
            onConnectionChange: connected => deliver({type: 'connection', connected}),
            onSessionEnd: () => deliver({type: 'session-end'}),
        }, import.meta.url);
        socket.connect();
        topics.forEach(topic => socket.subscribe(topic));
    };

    if (typeof SharedWorker !== 'undefined') {
        try {
            const worker = new SharedWorker(import.meta.url, {type: 'module', name: WORKER_NAME});
            worker.onerror = () => {
                // a browser with shared workers but without module ones ends up here
                port = null;
                startSocket();
            };
            worker.port.onmessage = e => {
                if (e.data && (typeof e.data.topic === 'string' || e.data.type === 'session-end' ||
                               e.data.type === 'connection')) {
                    deliver(e.data);
                }
            };
            worker.port.start();
            port = worker.port;
        }
        catch (e) {
            startSocket();
        }
    }
    else {
        startSocket();
    }

    return {
        subscribe(topic) {
            if (topics.has(topic)) {
                return;
            }
            topics.add(topic);
            if (port) {
                port.postMessage({type: 'subscribe', topic});
            }
            if (socket) {
                socket.subscribe(topic);
            }
        },
    };
}

function startSharedWorker() {
    const ports = new Set();

    // the union of every page's interest; never unsubscribed, as a shared worker is not told when
    // a page is gone
    const topics = new Set();

    const socket = new AdminEventsSocket(socketUrl(), {
        onEvent: event => broadcast({type: 'event', topic: event.topic, data: event.data}),
        onLoss: loss => broadcast({type: 'loss', topic: loss.topic, count: loss.count}),
        onConnectionChange: connected => broadcast({type: 'connection', connected}),
        onSessionEnd: () => broadcast({type: 'session-end'}),
    }, import.meta.url);
    socket.connect();

    function broadcast(message) {
        ports.forEach(port => {
            try {
                port.postMessage(message);
            } catch (e) {
                ports.delete(port);
            }
        });
    }

    self.onconnect = event => {
        const port = event.ports[0];
        ports.add(port);

        if (socket.sessionEnded) {
            socket.connect();
        } else if (socket.connected === false) {
            port.postMessage({type: 'connection', connected: false});
        }

        port.onmessage = e => {
            const message = e.data;
            if (message && message.type === 'subscribe' && typeof message.topic === 'string') {
                if (!topics.has(message.topic)) {
                    topics.add(message.topic);
                }
                socket.subscribe(message.topic);
            }
        };

        port.start();
    };
}

if (typeof SharedWorkerGlobalScope !== 'undefined' && self instanceof SharedWorkerGlobalScope) {
    startSharedWorker();
}
