declare global {
    interface XpLibraries {
        '/lib/xp/event': typeof import('./event');
    }
}

export interface ListenerParams<EventData extends object> {
    /**
     * Event type pattern
     */
    type: string;

    /**
     * Callback event listener
     */
    callback: (event: EnonicEvent<EventData>) => void;

    /**
     * Local events only (default to false)
     */
    localOnly?: boolean;
}

export interface SendParams<EventData extends object> {
    /**
     * Event type
     */
    type: string;

    /**
     * `true` if it should be distributed in cluster
     */
    distributed?: boolean;

    /**
     * Additional data for event.
     */
    data?: EventData;
}

export interface EnonicEvent<EventData extends object = EnonicEventData> {
    type: string;
    timestamp: number;
    localOrigin: boolean;
    distributed: boolean;
    data: EventData;
}

export interface EnonicEventData {
    nodes: EnonicEventDataNode[];
    state?: string; // event type <'node.stateUpdated'>
}

export interface EnonicEventDataNode {
    id: string;
    path: string;
    branch: string;
    repo: string;
    newPath?: string; // event type <'node.moved' | 'node.renamed'>
}

interface EventSenderHelper {
    setType(type: string): void;
    setDistributed(distributed: boolean): void;
    setData(value: object): void;
    send(): void;
}

interface EventListenerHelper {
    setType(type: string): void;
    setListener<T = unknown>(callback: (event: T) => void): void;
    setLocalOnly(localOnly: boolean): void;
    register(): void;
}

/**
 * Event functions.
 *
 * @example
 * var eventLib = require('/lib/xp/event');
 *
 * @module event
 */

/**
 * This function adds a event listener.
 *
 * @example-ref examples/event/listener.js
 *
 * @param {object} params Listener parameters.
 * @param {string} params.type Event type pattern.
 * @param {function} params.callback Callback event listener.
 * @param {boolean} params.localOnly Local events only (default to false).
 */
export function listener<EventData extends object = EnonicEventData>(params: ListenerParams<EventData>): void {
    const helper: EventListenerHelper = __.newBean<EventListenerHelper>('com.enonic.xp.lib.event.EventListenerHelper');

    helper.setType(params.type ?? '');
    helper.setLocalOnly(params.localOnly === true);
    helper.setListener((event: EnonicEvent<EventData>) => params.callback(__.toNativeObject(event)));

    helper.register();
}

/**
 * This function sends a custom event. All custom events are prefixed 'custom.'.
 *
 * @example-ref examples/event/send.js
 *
 * @param {object} event Event to send.
 * @param {string} event.type Event type.
 * @param {boolean} event.distributed True if it should be distributed in cluster.
 * @param {object} event.data Additional data for event.
 */
export function send<EventData extends object = object>(event: SendParams<EventData>): void {
    const helper: EventSenderHelper = __.newBean<EventSenderHelper>('com.enonic.xp.lib.event.EventSenderHelper');

    helper.setType(event.type ?? 'test');
    helper.setDistributed(event.distributed === true);

    if (event.data) {
        helper.setData(__.toScriptValue(event.data));
    }

    helper.send();
}
