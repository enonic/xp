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
 * var result = securityLib.runWith({
 *     branch: 'draft',
 *     user: 'su'
 *   },
 *   callback
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
    return bean.run(callback);
};


/**
 * Set permissions on a specified content.
 *
 * @example
 * securityLib.setPermissions({
 *   key: '03c6ae7b-7f48-45f5-973d-1f03606ab928',
 *   permissions: [{
 *     principal: 'user:system:anonymous',
 *     allow: ['READ'],
 *     deny: []
 *   }]
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or ID of the content.
 * @param {array} params.permissions Array of permissions.
 * @param {string} params.permissions.principal Principal key.
 * @param {array} params.permissions.allow Allowed permissions.
 * @param {array} params.permissions.deny Denied permissions.
 * @returns {object} Updated content.
 */
exports.setPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.security.SetPermissionsHandler');

    if (params.key) {
        bean.setKey(params.key);
    }
    if (params.permissions) {
        bean.permissions = __.toScriptValue(params.permissions);
    }
    return __.toNativeObject(bean.execute());
};
