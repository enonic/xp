/**
 * Event functions.
 *
 * @example
 * var eventLib = require('/lib/xp/event');
 *
 * @module event
 */
/* global __*/

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
exports.listener = function (params) {
    var helper = __.newBean('com.enonic.xp.lib.event.EventListenerHelper');

    helper.setType(params.type || '');
    helper.setLocalOnly(params.localOnly || false);
    helper.setListener(function (event) {
        params.callback(__.toNativeObject(event));
    });

    helper.register();
};

/**
 * This function sends a custom event. All custom events are prefixed "custom.".
 *
 * @example-ref examples/event/send.js
 *
 * @param {object} event Event to send.
 * @param {string} event.type Event type.
 * @param {boolean} event.distributed True if it should be distributed in cluster.
 * @param {object} event.data Additional data for event.
 */
exports.send = function (event) {
    var helper = __.newBean('com.enonic.xp.lib.event.EventSenderHelper');

    helper.setType(event.type || 'test');
    helper.setDistributed(event.distributed || false);

    if (event.data) {
        helper.setData(__.toScriptValue(event.data));
    }

    helper.send();
};
