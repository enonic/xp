/**
 * Functions to manage Content Projects.
 *
 * @example
 * var projectLib = require('/lib/xp/project');
 *
 * @module project
 */

/**
 @typedef ReadAccessDef
 @type {object}
 @property {boolean} [public] - Public read access (READ permissions for `system.everyone`). Defaults to `true`.
 */

/**
 @typedef PermissionsDef
 @type {object}
 @property {string} role - Role name (one of 'owner', 'editor', 'author', 'contributor', 'viewer')
 @property {array} principals - Array of principals
 */

/**
 * Creates a new Content Project.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id (alpha-numeric characters and hyphens allowed).
 * @param {string} [params.displayName] Project's display name. Defaults to `params.name`.
 * @param {string} [params.description] Project description.
 * @param {string} [params.language] Default project language.
 * @param {PermissionsDef} [params.permissions] Project permissions.
 * @param {ReadAccessDef} [params.readAccess] Read access settings.

 *
 * @returns {object} Created project.
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.CreateProjectHandler');
    bean.id = required(params, 'id');
    bean.displayName = nullOrValue(params.displayName);
    bean.description = nullOrValue(params.description);
    bean.language = nullOrValue(params.language);
    bean.permissions = __.toScriptValue(params.permissions);
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};

/**
 * Modifies an existing Content Project.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {string} [params.displayName] Project's display name.
 * @param {string} [params.description] Project description.
 * @param {string} [params.language] Default project language.

 *
 * @returns {object} Modified project.
 */
exports.modify = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ModifyProjectHandler');
    bean.id = required(params, 'id');
    bean.displayName = nullOrValue(params.displayName);
    bean.description = nullOrValue(params.description);
    bean.language = nullOrValue(params.language);
    return __.toNativeObject(bean.execute());
};

/**
 * Deletes an existing Content Project. This will delete all of the data inside the project repository.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.

 *
 * @returns {object} Deleted project.
 */
exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.DeleteProjectHandler');
    bean.id = required(params, 'id');
    return __.toNativeObject(bean.execute());
};

/**
 * Returns an existing Content Project.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.

 *
 * @returns {object} Content Project.
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.GetProjectHandler');
    bean.id = required(params, 'id');
    return __.toNativeObject(bean.execute());
};

/**
 * Returns all Content Projects.
 *

 *
 * @returns {object[]} Array of Content Projects.
 */
exports.list = function () {
    var bean = __.newBean('com.enonic.xp.lib.project.ListProjectsHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * Adds permissions to an existing Content Project.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {PermissionsDef} params.permissions Project permissions to add.

 *
 * @returns {object} Content Project.
 */
exports.addPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.AddProjectPermissionsHandler');
    bean.id = required(params, 'id');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

/**
 * Removes permissions from an existing Content Project.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {PermissionsDef} params.permissions Project permissions to remove.

 *
 * @returns {object} Content Project.
 */
exports.removePermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.RemoveProjectPermissionsHandler');
    bean.id = required(params, 'id');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

/**
 * Toggles public/private read access for an existing Content Project.
 * This will modify permissions on all of the content items inside the repository by adding or removing READ access for `system.everyone`.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {ReadAccessDef} params.readAccess Read access.

 *
 * @returns {object} Content Project.
 */
exports.modifyReadAccess = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ModifyProjectReadAccessHandler');
    bean.id = required(params, 'id');
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

