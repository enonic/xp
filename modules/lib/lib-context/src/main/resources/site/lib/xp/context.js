/**
 * Built-in context functions.
 *
 * @example
 * var contextLib = require('/lib/xp/context');
 *
 * @module lib/xp/context
 */

var bean = __.newBean('com.enonic.xp.lib.context.ContextHandlerBean');

/**
 * Runs a function within a specified context.
 *
 * @example
 * var result = contextLib.run({
 *     branch: 'draft',
 *     user: 'su'
 *   },
 *   callback
 * });
 *
 * @param {object} context JSON parameters.
 * @param {string} [context.branch] Name of the branch to execute the callback in.
 * @param {string} [context.user] Name of user to execute the callback with.
 * @param {function} callback Function to execute.
 * @returns {object} Result of the function execution.
 */
exports.run = function (context, callback) {
    var params = bean.newRunParams();
    params.callback = callback;

    if (context.branch) {
        params.branch = context.branch;
    }

    if (context.user) {
        params.user = context.user;
    }

    var result = bean.run(params);
    return __.toNativeObject(result);
};

/**
 * Returns the current context.
 *
 * @example
 * var result = contextLib.get();
 *
 * @returns {object} Return the current context as JSON object.
 */
exports.get = function () {
    var result = bean.get();
    return __.toNativeObject(result);
};
