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
 *     user: {
 *       login: 'su',
 *       userStore: 'system'
 *   },
 *   callback
 * });
 *
 * @param {object} context JSON parameters.
 * @param {string} [context.branch] Name of the branch to execute the callback in. Default is the current branch set in portal.
 * @param {object} [context.user] User to execute the callback with. Default is the current user.
 * @param {string} context.user.login Login of the user.
 * @param {string} [context.user.userStore] User store containing the user. By default, all the user stores will be used.
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
        if (context.user.login) {
            params.username = context.user.login;
        }
        if (context.user.userStore) {
            params.userStore = context.user.userStore;
        }
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
