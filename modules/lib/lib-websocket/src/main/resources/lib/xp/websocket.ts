/**
 * Websocket functions.
 *
 * @example
 * var webSocketLib = require('/lib/xp/websocket');
 *
 * @module websocket
 */

declare global {
    interface XpLibraries {
        '/lib/xp/websocket': typeof import('./websocket');
    }
}

interface WebSocketManagerBean {
    addToGroup(group: string, id: string): void;

    removeFromGroup(group: string, id: string): void;

    send(id: string, message: string): void;

    sendToGroup(group: string, message: string): void;

    getGroupSize(group: string): number;
}

/**
 * Add an id to a socket group.
 *
 * @example-ref examples/websocket/addToGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Socket id.
 */
export function addToGroup(group: string, id: string): void {
    const bean: WebSocketManagerBean = __.newBean<WebSocketManagerBean>('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.addToGroup(group, id);
}

/**
 * Remove an id from a socket group.
 *
 * @example-ref examples/websocket/removeFromGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Socket id.
 */
export function removeFromGroup(group: string, id: string): void {
    const bean: WebSocketManagerBean = __.newBean<WebSocketManagerBean>('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.removeFromGroup(group, id);
}

/**
 * Send message directly to a socket id.
 *
 * @example-ref examples/websocket/send.js
 *
 * @param {string} id Socket id.
 * @param {string} message Message as text.
 */
export function send(id: string, message: string): void {
    const bean: WebSocketManagerBean = __.newBean<WebSocketManagerBean>('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.send(id, message);
}

/**
 * Send message to all sockets in group.
 *
 * @example-ref examples/websocket/sendToGroup.js
 *
 * @param {string} group Group name.
 * @param {string} message Message as text.
 */
export function sendToGroup(group: string, message: string): void {
    const bean: WebSocketManagerBean = __.newBean<WebSocketManagerBean>('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.sendToGroup(group, message);
}

/**
 * Get number of all sockets in group.
 *
 * @example-ref examples/websocket/addToGroup.js
 *
 * @param {string} group Group name.
 */
export function getGroupSize(group: string): number {
    const bean: WebSocketManagerBean = __.newBean<WebSocketManagerBean>('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    return bean.getGroupSize(group);
}
