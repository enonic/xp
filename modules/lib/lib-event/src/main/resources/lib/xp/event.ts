declare global {
    interface XpLibraries {
        '/lib/xp/event': typeof import('./event');
    }
}

export interface ListenerParams<EventData extends object = EnonicEventData> {
    /**
     * Event type pattern
     */
    type: EnonicEventTypes;

    /**
     * Callback event listener
     */
    callback: (event: EnonicEvent<EventData>) => void;

    /**
     * Local events only (default to false)
     */
    localOnly?: boolean;
}

export interface SendParams {
    /**
     * Event type
     */
    type: EnonicEventTypes;

    /**
     * `true` if it should be distributed in cluster
     */
    distributed?: boolean;

    /**
     * Additional data for event.
     */
    data?: object;
}

export interface EnonicEvent<EventData extends object = EnonicEventData> {
    readonly type: EnonicEventTypes | string;
    readonly timestamp: number;
    readonly localOrigin: boolean;
    readonly distributed: boolean;
    readonly data: EventData;
}

export interface EnonicEventData {
    readonly nodes: ReadonlyArray<EnonicEventDataNode>;
    readonly state?: string; // event type <'node.stateUpdated'>
}

export interface EnonicEventDataNode {
    readonly id: string;
    readonly path: string;
    readonly branch: string;
    readonly repo: string;
    readonly newPath?: string; // event type <'node.moved' | 'node.renamed'>
}

type LiteralUnion<T extends U, U = string> = T | (U & Record<never, never>);

export type EnonicEventTypes =
    LiteralUnion<
    | 'node.*'
    | 'node.created'
    | 'node.deleted'
    | 'node.pushed'
    | 'node.duplicated'
    | 'node.updated'
    | 'node.moved'
    | 'node.renamed'
    | 'node.sorted'
    | 'node.stateUpdated'
    | 'node.permissionsUpdated'
    >;

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
    const helper = __.newBean<EventListenerHelper>('com.enonic.xp.lib.event.EventListenerHelper');

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
export function send(event: SendParams): void {
    const helper = __.newBean<EventSenderHelper>('com.enonic.xp.lib.event.EventSenderHelper');

    helper.setType(event.type ?? 'test');
    helper.setDistributed(event.distributed === true);

    if (event.data) {
        helper.setData(__.toScriptValue(event.data));
    }

    helper.send();
}