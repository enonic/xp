/**
 * Built-in authentication functions.
 *
 * @example
 * var authLib = require('/lib/xp/auth');
 *
 * @module lib/xp/auth
 */

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }

    return value;
}

/**
 * Login a user with the specified userStore, userName and password.
 *
 * @example
 * var user = authLib.login({
 *   user: 'dummy',
 *   userStore: 'mystore',
 *   password: 'secret'
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.user Name of user to log in.
 * @param {string} params.userStore Name of user-store where the user is stored. If not specified it will try all available user-stores in order.
 * @param {string} params.password Password for the user.
 * @returns {object} Information for logged-in user.
 */
exports.login = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.LoginHandler');

    bean.user = required(params, 'user');
    bean.password = required(params, 'password');

    if (params['userStore']) {
        bean.userStore = [].concat(params['userStore']);
    }

    return __.toNativeObject(bean.login());
};

/**
 * Logout an already logged-in user.
 *
 * @example
 * authLib.logout();
 */
exports.logout = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.LogoutHandler');

    bean.logout();
};

/**
 * Returns the logged-in user. If not logged-in, this will return *undefined*.
 *
 * @example
 * var user = authLib.getUser();
 *
 * @returns {object} Information for logged-in user.
 */
exports.getUser = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetUserHandler');

    return __.toNativeObject(bean.getUser());
};

/**
 * Checks if the logged-in user has the specified role.
 *
 * @example
 * var hasAdmin = authLib.hasRole('admin');
 *
 * @param {string} role Role to check for.
 * @returns {boolean} True if the user has specfied role, false otherwise.
 */
exports.hasRole = function (role) {
    var bean = __.newBean('com.enonic.xp.lib.auth.HasRoleHandler');

    bean.role = __.nullOrValue(role);

    return bean.hasRole();
};

/**
 * Generates a secure password.
 *
 * @example
 * var password = authLib.generatePassword();
 *
 * @returns {string} A secure generated password.
 */
exports.generatePassword = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.GeneratePasswordHandler');

    return __.toNativeObject(bean.generatePassword());
};

/**
 * Changes password for specified user.
 *
 * @example
 * authLib.changePassword({
 *   userKey: 'some-user-key',
 *   password: 'secret'
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.userKey Key for user to change password.
 * @param {string} params.password New password to set.
 */
exports.changePassword = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ChangePasswordHandler');

    bean.userKey = required(params, 'userKey');
    bean.password = required(params, 'password');

    bean.changePassword();
};

exports.getPrincipal = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetPrincipalHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getPrincipal());
};

exports.getMemberships = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetMembershipsHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getMemberships());
};

exports.createUser = function (userStore, name, displayName, email) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateUserHandler');

    bean.userStore = __.nullOrValue(userStore);

    bean.name = __.nullOrValue(name);

    bean.displayName = __.nullOrValue(displayName);

    bean.email = __.nullOrValue(email);

    return __.toNativeObject(bean.createUser());
};

exports.createGroup = function (userStore, groupName, displayName) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateGroupHandler');

    bean.userStore = __.nullOrValue(userStore);

    bean.name = __.nullOrValue(groupName);

    bean.displayName = __.nullOrValue(displayName);

    return __.toNativeObject(bean.createGroup());
};

