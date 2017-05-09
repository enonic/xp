/**
 * Websocket functions.
 *
 * @example
 * var webSocketLib = require('/lib/xp/websocket');
 *
 * @module websocket
 */

/**
 * Add an id to a socket group.
 *
 * @example-ref examples/websocket/addToGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Socket id.
 */
exports.addToGroup = function (group, id) {
    var bean = __.newBean('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.addToGroup(group, id);
};

/**
 * Remove an id from a socket group.
 *
 * @example-ref examples/websocket/removeFromGroup.js
 *
 * @param {string} group Group name.
 * @param {string} id Socket id.
 */
exports.removeFromGroup = function (group, id) {
    var bean = __.newBean('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.removeFromGroup(group, id);
};

/**
 * Send message directly to a socket id.
 *
 * @example-ref examples/websocket/send.js
 *
 * @param {string} id Socket id.
 * @param {string} message Message as text.
 */
exports.send = function (id, message) {
    var bean = __.newBean('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.send(id, message);
};

/**
 * Send message to all sockets in group.
 *
 * @example-ref examples/websocket/sendToGroup.js
 *
 * @param {string} group Group name.
 * @param {string} message Message as text.
 */
exports.sendToGroup = function (group, message) {
    var bean = __.newBean('com.enonic.xp.lib.websocket.WebSocketManagerBean');
    bean.sendToGroup(group, message);
};
