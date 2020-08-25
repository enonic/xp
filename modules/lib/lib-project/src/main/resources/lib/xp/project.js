/**
 * Functions to manage Content Projects.
 *
 * @example
 * var projectLib = require('/lib/xp/project');
 *
 * @module project
 */

/* global __ */

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

/**
 * Creates a new Content Project. Only `system.admin` and `cms.admin` roles have permissions to create new projects.
 *
 * @example-ref examples/project/create.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id (alpha-numeric characters and hyphens allowed).
 * @param {string} params.displayName Project's display name.
 * @param {string} [params.description] Project description.
 * @param {string} [params.language] Default project language.
 * @param {Object.<string, string[]>} [params.permissions] Project permissions. 1 to 5 properties where key is role id and value is an array of principals.
 * @param {string} params.permissions.role - Role id (one of `owner`, `editor`, `author`, `contributor`, `viewer`)
 * @param {string[]} params.permissions.principals - Array of principals
 * @param {Object<string, boolean>} [params.readAccess] Read access settings.
 * @param {boolean} params.readAccess.public Public read access (READ permissions for `system.everyone`).
 *
 * @returns {Object} Created project.
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.CreateProjectHandler');
    bean.id = required(params, 'id');
    bean.displayName = required(params, 'displayName');
    bean.description = nullOrValue(params.description);
    bean.language = nullOrValue(params.language);
    bean.permissions = __.toScriptValue(params.permissions);
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};

/**
 * Modifies an existing Content Project.
 * To modify a project, user must have `owner` permissions for this project, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/modify.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {string} [params.displayName] Project's display name.
 * @param {string} [params.description] Project description.
 * @param {string} [params.language] Default project language.
 *
 * @returns {Object} Modified project.
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
 * To delete a project, user must have either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/delete.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 *
 * @returns {boolean} `true` if the project was successfully deleted.
 */
exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.DeleteProjectHandler');
    bean.id = required(params, 'id');
    return __.toNativeObject(bean.execute());
};

/**
 * Returns an existing Content Project.
 * To `get` a project, user must be a member of one of the project roles, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/get.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 *
 * @returns {Object} Content Project object or `null` if not found.
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.GetProjectHandler');
    bean.id = required(params, 'id');
    return __.toNativeObject(bean.execute());
};

/**
 * Returns all Content Projects.
 * The list will be limited to projects that user has permissions for.
 * Users with `system.admin` or `cms.admin` roles will get the list of all projects.
 *
 * @example-ref examples/project/list.js
 *
 * @returns {Object[]} Array of Content Project objects.
 */
exports.list = function () {
    var bean = __.newBean('com.enonic.xp.lib.project.ListProjectsHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * Adds permissions to an existing Content Project.
 * To modify permissions, user must have `owner` permissions for the project, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/addPermissions.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {Object.<string, string[]>} params.permissions Project permissions to add. 1 to 5 properties where key is role id and value is an array of principals.
 * @param {string} params.permissions.role - Role id (one of `owner`, `editor`, `author`, `contributor`, `viewer`)
 * @param {Object[]} params.permissions.principals - Array of principals to add to this role

 *
 * @returns {Object} All current project permissions.
 */
exports.addPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.AddProjectPermissionsHandler');
    bean.id = required(params, 'id');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

/**
 * Removes permissions from an existing Content Project.
 * To modify permissions, user must have `owner` permissions for the project, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/removePermissions.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {Object.<string, string[]>} params.permissions Project permissions to delete. 1 to 5 properties where key is role id and value is an array of principals.
 * @param {string} params.permissions.role - Role id (one of `owner`, `editor`, `author`, `contributor`, `viewer`)
 * @param {Object[]} params.permissions.principals - Array of principals to delete from this role
 *
 * @returns {Object} All current project permissions.
 */
exports.removePermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.RemoveProjectPermissionsHandler');
    bean.id = required(params, 'id');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

/**
 * Toggles public/private READ access for an existing Content Project.
 * This will modify permissions on ALL of the content items inside the project repository by adding or removing READ access for `system.everyone`.
 * To modify READ access, user must have `owner` permissions for the project, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/modifyReadAccess.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 * @param {Object<string, boolean>} params.readAccess READ access.
 * @param {boolean} params.readAccess.public Public read access (READ permissions for `system.everyone`).
 *
 * @returns {Object<string, boolean>} Current state of READ access.
 */
exports.modifyReadAccess = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ModifyProjectReadAccessHandler');
    bean.id = required(params, 'id');
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};
