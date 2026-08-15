/**
 * Client for the admin events hub, served by the hub itself at `<admin:events>/client.js`.
 *
 * Imported by a page, it exports `AdminEventsSocket`. Started as a shared worker from the same
 * url, it holds one socket for every admin page of the browser and forwards what arrives to each
 * connected port. The url is what makes a shared worker shared, so every admin tool must load
 * this one rather than a copy of its own.
 *
 * Port protocol. To the worker: `{type: 'subscribe', topic}`. From the worker:
 * `{type: 'event', topic, data}` and `{type: 'loss', topic, count}`, where `count` is null when
 * the number of missed messages cannot be known.
 */

const WS_PROTOCOL = 'json';

const CONNECTION_TIMEOUT = 60000;
const PING_INTERVAL = 50000;
const PONG_TIMEOUT = 15000;
const MAX_RECONNECT_DELAY = 30000;
const MAX_SUBSCRIBE_RETRY_DELAY = 30000;

/**
 * Holds the wanted-subscription set and resubscribes it on every reconnect. A subscription denied
 * as `unknown` is retried with backoff; `forbidden` ends retries until the next reconnect. Loss is
 * reported per subscription: a sequence gap within an epoch is countable, an epoch change is not.
 */
export class AdminEventsSocket {

    constructor(url, handlers) {
        this.url = url;
        this.handlers = handlers;
        this.subscriptions = new Map();
        this.ws = null;
        this.reconnectAttempts = 0;
        this.closed = false;
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

        if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
            return;
        }

        const ws = new WebSocket(this.url, [WS_PROTOCOL]);
        this.ws = ws;

        this.connectionTimeout = setTimeout(() => {
            if (ws.readyState === WebSocket.CONNECTING) {
                ws.close();
            }
        }, CONNECTION_TIMEOUT);

        ws.onopen = () => {
            clearTimeout(this.connectionTimeout);
            this.reconnectAttempts = 0;
            this.resubscribeAll();

            this.pingInterval = setInterval(() => {
                this.send({type: 'ping'});
                clearTimeout(this.pongTimeout);
                this.pongTimeout = setTimeout(() => this.reconnect(), PONG_TIMEOUT);
            }, PING_INTERVAL);
        };

        ws.onmessage = e => this.handleMessage(e);

        ws.onclose = () => {
            clearTimeout(this.connectionTimeout);
            this.teardownTimers();
            if (!this.closed) {
                this.scheduleReconnect();
            }
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
    const href = self.location.href;
    const base = href.substring(0, href.lastIndexOf('/'));
    return base.replace(/^http/, 'ws');
}

function startSharedWorker() {
    const ports = new Set();

    // the union of every page's interest; never unsubscribed, as a shared worker is not told when
    // a page is gone
    const topics = new Set();

    const socket = new AdminEventsSocket(socketUrl(), {
        onEvent: event => broadcast({type: 'event', topic: event.topic, data: event.data}),
        onLoss: loss => broadcast({type: 'loss', topic: loss.topic, count: loss.count}),
    });
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
