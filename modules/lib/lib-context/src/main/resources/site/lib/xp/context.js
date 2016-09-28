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
 * @example-ref examples/context/run.js
 *
 * @param {object} context JSON parameters.
 * @param {string} [context.repository] Repository to execute the callback in. Default is the current repository set in portal.
 * @param {string} [context.branch] Name of the branch to execute the callback in. Default is the current branch set in portal.
 * @param {object} [context.user] User to execute the callback with. Default is the current user.
 * @param {string} context.user.login Login of the user.
 * @param {string} [context.user.userStore] User store containing the user. By default, all the user stores will be used.
 * @param {array} [context.principals] Additional principals to execute the callback with.
 * @param {function} callback Function to execute.
 * @returns {object} Result of the function execution.
 */
exports.run = function (context, callback) {
    var params = bean.newRunParams();
    params.callback = callback;

    if (context.repository) {
        params.repository = context.repository;
    }

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

    if (context.principals) {
        params.principals = context.principals;
    }

    var result = bean.run(params);
    return __.toNativeObject(result);
};

/**
 * Returns the current context.
 *
 * @example-ref examples/context/get.js
 *
 * @returns {object} Return the current context as JSON object.
 */
exports.get = function () {
    var result = bean.get();
    return __.toNativeObject(result);
};
