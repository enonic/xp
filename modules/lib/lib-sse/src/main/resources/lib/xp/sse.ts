/**
 * Server-Sent Events (SSE) functions.
 *
 * @example
 * var sseLib = require('/lib/xp/sse');
 *
 * @module sse
 */

declare global {
    interface XpLibraries {
        '/lib/xp/sse': typeof import('./sse');
    }
}

interface SseManagerBean {
    send(id: string, event: string | null, data: string, eventId: string | null): void;

    sendToGroup(group: string, event: string | null, data: string): void;

    close(id: string): void;

    addToGroup(group: string, id: string): void;

    removeFromGroup(group: string, id: string): void;

    getGroupSize(group: string): number;
}

export interface SendParams {
    id: string;
    event?: string;
    data: string;
    eventId?: string;
}

export interface SendToGroupParams {
    group: string;
    event?: string;
    data: string;
}

/**
 * Send an event to a specific SSE connection.
 *
 * @example-ref examples/sse/send.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Connection id.
 * @param {string} [params.event] Event name.
 * @param {string} params.data Event data.
 * @param {string} [params.eventId] Event id for last-event-id tracking.
 */
export function send(params: SendParams): void {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.send(params.id, params.event != null ? params.event : null, params.data, params.eventId != null ? params.eventId : null);
}

/**
 * Send an event to all connections in a group.
 *
 * @example-ref examples/sse/sendToGroup.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.group Group name.
 * @param {string} [params.event] Event name.
 * @param {string} params.data Event data.
 */
export function sendToGroup(params: SendToGroupParams): void {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.sendToGroup(params.group, params.event != null ? params.event : null, params.data);
}

/**
 * Close an SSE connection.
 *
 * @example-ref examples/sse/close.js
 *
 * @param {string} id Connection id.
 */
export function close(id: string): void {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.close(id);
}

/**
 * Add a connection to a group.
 *
 * @example-ref examples/sse/addToGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Connection id.
 */
export function addToGroup(group: string, id: string): void {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.addToGroup(group, id);
}

/**
 * Remove a connection from a group.
 *
 * @example-ref examples/sse/removeFromGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Connection id.
 */
export function removeFromGroup(group: string, id: string): void {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.removeFromGroup(group, id);
}

/**
 * Get number of connections in a group.
 *
 * @example-ref examples/sse/addToGroup.js
 *
 * @param {string} group Group name.
 */
export function getGroupSize(group: string): number {
    const bean: SseManagerBean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    return bean.getGroupSize(group);
}
