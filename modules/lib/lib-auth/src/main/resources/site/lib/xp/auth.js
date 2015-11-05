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

function nullOrValue(value) {
    if (value === undefined) {
        return null;
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


/**
 * Finds principal with given key or null if it doesn't exist.
 *
 * @example
 * authLib.getPrincipal('principal-key');
 *
 * @param {string} principalKey Principal key to look for.
 */
exports.getPrincipal = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetPrincipalHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getPrincipal());
};

/**
 * Returns list of membership principals for given key.
 *
 * @example
 * authLib.getMemberships('principal-key');
 *
 * @param {string} principalKey Principal key to look for.
 */
exports.getMemberships = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetMembershipsHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getMemberships());
};

/**
 * Creates user from passed parameters.
 *
 * @example
 * authLib.createUser({
 *   userStore: 'user-store-key',
 *   name: 'user-id',
 *   displayName: 'user-display-name',
 *   email: 'email'
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.userStore Key for user store where user has to be created.
 * @param {string} params.name User login name to set.
 * @param {string} params.displayName User display name.
 * @param {string} params.email User email.
 */
exports.createUser = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateUserHandler');

    bean.userStore = required(params, 'userStore');

    bean.name = required(params, 'name');

    bean.displayName = nullOrValue(params.displayName);

    bean.email = nullOrValue(params.email);

    return __.toNativeObject(bean.createUser());
};

/**
 * Modifies user with passed parameters.
 *
 * @example
 * authLib.modifyUser({
 *   key: 'user-key',
 *   editor: function(user) {
 *     user.displayName = 'new-display-name';
 *     user.email = 'new-email';
 *     return user;
 *   }
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} key Principal key of the user to modify.
 * @param {string} params.editor User editor function to apply to user.
 */
exports.modifyUser = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ModifyUserHandler');

    bean.principalKey = required(params, 'key');

    bean.editor = __.toScriptValue(params.editor);

    return __.toNativeObject(bean.modifyUser());
};

/**
 * Creates group from passed parameters.
 *
 * @example
 * authLib.createGroup({
 *   userStore: 'user-store',
 *   name: 'group-name',
 *   displayName: 'group-display-name'
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.userStore Key for user store where group has to be created.
 * @param {string} params.name Group name.
 * @param {string} params.displayName Group display name.
 */
exports.createGroup = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateGroupHandler');

    bean.userStore = required(params, 'userStore');

    bean.name = required(params, 'name');

    bean.displayName = nullOrValue(params.displayName);

    return __.toNativeObject(bean.createGroup());
};

