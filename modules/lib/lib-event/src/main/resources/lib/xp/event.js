/**
 * Event functions.
 *
 * @example
 * var eventLib = require('/lib/xp/event');
 *
 * @module lib/xp/event
 */

var helper = __.newBean('com.enonic.xp.lib.event.EventLibHelper');

/**
 * This function adds a event listener.
 *
 * @example-ref examples/event/listener.js
 *
 * @param {string} pattern Event type pattern.
 * @param {function} listener Callback event listener.
 */
exports.listener = function (pattern, listener) {
    helper.listener(pattern, function (event) {
        listener(__.toNativeObject(event));
    });
};
