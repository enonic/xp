/**
 * Built-in authentication functions.
 *
 * @example
 * var authLib = require('/lib/xp/auth');
 *
 * @module lib/xp/auth
 */

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

/**
 * Run a function received as parameter in a specified context.
 *
 * @example
 * var user = securityLib.runWith({
 *   context : {
 *     branch: 'draft',
 *     user: 'su'
 *   },
 *   callback: myFunction
 * });
 *
 * @param {object} context JSON parameters.
 * @param {string} context.branch Name of the branch to execute the callback in.
 * @param {string} context.user Name of user to execute the callback with.
 * @param {function} callback Function to execute.
 * @returns {object} Result of the function execution.
 */
exports.runWith = function (context, callback) {
    var bean = __.newBean('com.enonic.xp.lib.security.RunWithHandler');

    if (context.branch) {
        bean.setBranch(context.branch);
    }
    if (context.user) {
        bean.setUser(context.user);
    }
    bean.run(callback);
};
