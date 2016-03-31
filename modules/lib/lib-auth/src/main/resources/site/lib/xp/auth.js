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
 * @example-ref examples/auth/login.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.user Name of user to log in.
 * @param {string} [params.userStore] Name of user-store where the user is stored. If not specified it will try all available user-stores in order.
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
 * @example-ref examples/auth/logout.js
 */
exports.logout = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.LogoutHandler');

    bean.logout();
};

/**
 * Returns the logged-in user. If not logged-in, this will return *undefined*.
 *
 * @example-ref examples/auth/getUser.js
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
 * @example-ref examples/auth/hasRole.js
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
 * @example-ref examples/auth/generatePassword.js
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
 * @example-ref examples/auth/changePassword.js
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
 * Returns the principal with the specified key.
 *
 * @example-ref examples/auth/getPrincipal.js
 *
 * @param {string} principalKey Principal key to look for.
 * @returns {object} the principal specified, or null if it doesn't exist.
 */
exports.getPrincipal = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetPrincipalHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getPrincipal());
};

/**
 * Returns a list of principals the specified principal is a member of.
 *
 * @example-ref examples/auth/getMemberships.js
 *
 * @param {string} principalKey Principal key to retrieve memberships for.
 * @returns {object[]} Returns the list of principals.
 */
exports.getMemberships = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetMembershipsHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getMemberships());
};

/**
 * Returns a list of principals that are members of the specified principal.
 *
 * @example-ref examples/auth/getMembers.js
 *
 * @param {string} principalKey Principal key to retrieve members for.
 * @returns {object[]} Returns the list of principals.
 */
exports.getMembers = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetMembersHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getMembers());
};

/**
 * Creates user from passed parameters.
 *
 * @example-ref examples/auth/createUser.js
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
 * Retrieves the user specified and updates it with the changes applied.
 *
 * @example-ref examples/auth/modifyUser.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the user to modify.
 * @param {function} params.editor User editor function to apply to user.
 * @returns {object} the updated user.
 */
exports.modifyUser = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ModifyUserHandler');

    bean.principalKey = required(params, 'key');
    bean.editor = __.toScriptValue(required(params, 'editor'));

    return __.toNativeObject(bean.modifyUser());
};

/**
 * Creates a group.
 *
 * @example-ref examples/auth/createGroup.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.userStore Key for user store where group has to be created.
 * @param {string} params.name Group name.
 * @param {string} params.displayName Group display name.
 * @param {string} params.description as principal description .
 */
exports.createGroup = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateGroupHandler');

    bean.userStore = required(params, 'userStore');
    bean.name = required(params, 'name');
    bean.displayName = nullOrValue(params.displayName);
    bean.description = nullOrValue(params.description);

    return __.toNativeObject(bean.createGroup());
};

/**
 * Retrieves the group specified and updates it with the changes applied.
 *
 * @example-ref examples/auth/modifyGroup.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the group to modify.
 * @param {function} params.editor Group editor function to apply to group.
 * @returns {object} the updated group.
 */
exports.modifyGroup = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ModifyGroupHandler');

    bean.principalKey = required(params, 'key');
    bean.editor = __.toScriptValue(required(params, 'editor'));

    return __.toNativeObject(bean.modifyGroup());
};

/**
 * Adds members to a principal (user or role).
 *
 * @example-ref examples/auth/addMembers.js
 *
 * @param {string} principalKey Key of the principal to add members to.
 * @param {string} members Keys of the principals to add.
 */
exports.addMembers = function (principalKey, members) {
    var bean = __.newBean('com.enonic.xp.lib.auth.AddMembersHandler');

    bean.principalKey = nullOrValue(principalKey);
    bean.members = [].concat(__.nullOrValue(members));

    return __.toNativeObject(bean.addMembers());
};

/**
 * Removes members from a principal (user or role).
 *
 * @example-ref examples/auth/removeMembers.js
 *
 * @param {string} principalKey Key of the principal to remove members from.
 * @param {string} members Keys of the principals to remove.
 */
exports.removeMembers = function (principalKey, members) {
    var bean = __.newBean('com.enonic.xp.lib.auth.RemoveMembersHandler');

    bean.principalKey = nullOrValue(principalKey);
    bean.members = [].concat(__.nullOrValue(members));

    return __.toNativeObject(bean.removeMembers());
};

/**
 * Search for principals matching the specified criteria.
 *
 * @example-ref examples/auth/findPrincipals.js
 *
 * @param {object} params JSON parameters.
 * @param {string} [params.type] Principal type to look for, one of: 'user', 'group' or 'role'. If not specified all principal types will be included.
 * @param {string} [params.userStore] Key of the user store to look for. If not specified all user stores will be included.
 * @param {string} [params.start] First principal to return from the search results. It can be used for pagination.
 * @param {string} [params.count] A limit on the number of principals to be returned.
 * @param {string} [params.name] Name of the principal to look for.
 * @param {string} [params.searchText] Text to look for in any principal field.
 * @returns {object} The "total" number of principals matching the search, the "count" of principals included, and an array of "hits" containing the principals.
 */
exports.findPrincipals = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.FindPrincipalsHandler');

    bean.type = __.nullOrValue(params.type);
    bean.userStore = __.nullOrValue(params.userStore);
    bean.start = __.nullOrValue(params.start);
    bean.count = __.nullOrValue(params.count);
    bean.name = __.nullOrValue(params.name);
    bean.searchText = __.nullOrValue(params.searchText);

    return __.toNativeObject(bean.findPrincipals());
};
