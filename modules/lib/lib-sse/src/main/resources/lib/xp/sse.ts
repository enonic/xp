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
    send(clientId: string, id: string | null, event: string | null, data: string | null, comment: string | null): void;

    sendToGroup(group: string, id: string | null, event: string | null, data: string | null, comment: string | null): void;

    close(clientId: string): void;

    isOpen(clientId: string): boolean;

    addToGroup(group: string, clientId: string): void;

    removeFromGroup(group: string, clientId: string): void;

    getGroupSize(group: string): number;
}

export interface SseMessage {
    id?: string;
    event?: string;
    data?: string;
    comment?: string;
}

export interface SendParams {
    clientId: string;
    message: SseMessage;
}

export interface SendToGroupParams {
    group: string;
    message: SseMessage;
}

export interface CloseParams {
    clientId: string;
}

export interface IsOpenParams {
    clientId: string;
}

export interface AddToGroupParams {
    group: string;
    clientId: string;
}

export interface RemoveFromGroupParams {
    group: string;
    clientId: string;
}

export interface GetGroupSizeParams {
    group: string;
}

/**
 * Send a message to a specific SSE connection. A message with empty data
 * will not dispatch an event on the client, but any `id` still updates the
 * client's last-event-id buffer. A `comment` emits an SSE comment line
 * which clients ignore (useful for keep-alive pings).
 *
 * @example-ref examples/sse/send.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.clientId Client id.
 * @param {object} params.message SSE message.
 * @param {string} [params.message.event] Event name.
 * @param {string} [params.message.data] Event data.
 * @param {string} [params.message.id] Event id for last-event-id tracking.
 * @param {string} [params.message.comment] Comment line (ignored by clients).
 */
export function send(params: SendParams): void {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    const msg = params.message;
    bean.send(params.clientId, __.nullOrValue(msg.id), __.nullOrValue(msg.event), __.nullOrValue(msg.data), __.nullOrValue(msg.comment));
}

/**
 * Send a message to all connections in a group. A message with empty data
 * will not dispatch an event on the client, but any `id` still updates the
 * client's last-event-id buffer. A `comment` emits an SSE comment line
 * which clients ignore (useful for keep-alive pings).
 *
 * @example-ref examples/sse/sendToGroup.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.group Group name.
 * @param {object} params.message SSE message.
 * @param {string} [params.message.event] Event name.
 * @param {string} [params.message.data] Event data.
 * @param {string} [params.message.id] Event id for last-event-id tracking.
 * @param {string} [params.message.comment] Comment line (ignored by clients).
 */
export function sendToGroup(params: SendToGroupParams): void {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    const msg = params.message;
    bean.sendToGroup(params.group, __.nullOrValue(msg.id), __.nullOrValue(msg.event), __.nullOrValue(msg.data),
        __.nullOrValue(msg.comment));
}

/**
 * Close an SSE connection.
 *
 * @example-ref examples/sse/close.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.clientId Client id.
 */
export function close(params: CloseParams): void {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.close(params.clientId);
}

/**
 * Check if an SSE connection is still open.
 * Use this to abort long-running work (expensive computation, large data
 * generation) when the client has disconnected. Do not use it as a guard
 * before `send` — send is already a safe no-op for closed connections and
 * adding a check only introduces a race.
 *
 * @example-ref examples/sse/isOpen.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.clientId Client id.
 * @returns {boolean} True if the connection is still open.
 */
export function isOpen(params: IsOpenParams): boolean {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    return bean.isOpen(params.clientId);
}

/**
 * Add a connection to a group.
 *
 * @example-ref examples/sse/addToGroup.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.group Group name.
 * @param {string} params.clientId Client id.
 */
export function addToGroup(params: AddToGroupParams): void {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.addToGroup(params.group, params.clientId);
}

/**
 * Remove a connection from a group.
 *
 * @example-ref examples/sse/removeFromGroup.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.group Group name.
 * @param {string} params.clientId Client id.
 */
export function removeFromGroup(params: RemoveFromGroupParams): void {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    bean.removeFromGroup(params.group, params.clientId);
}

/**
 * Get number of connections in a group.
 *
 * @example-ref examples/sse/addToGroup.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.group Group name.
 */
export function getGroupSize(params: GetGroupSizeParams): number {
    const bean = __.newBean<SseManagerBean>('com.enonic.xp.lib.sse.SseManagerBean');
    return bean.getGroupSize(params.group);
}
