/**
 * Functions to manage Content Projects.
 *
 * @example
 * var projectLib = require('/lib/xp/project');
 *
 * @module project
 */

declare global {
    interface XpLibraries {
        '/lib/xp/project': typeof import('./project');
    }
}

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export type ProjectRole = 'owner' | 'editor' | 'author' | 'contributor' | 'viewer';

export type ProjectPermission = Record<ProjectRole, string[]>;

export interface ProjectPermissions {
    permissions?: Record<ProjectRole, string[]>;
}

export interface ProjectReadAccess {
    public: boolean;
}

export interface CreateProjectParams {
    id: string;
    displayName: string;
    description?: string;
    language?: string;
    parent?: string;
    siteConfig: Record<string, unknown>;
    applications?: string[];
    permissions?: ProjectPermission;
    readAccess?: ProjectReadAccess;
}

export interface Project {
    id: string;
    displayName: string;
    description: string;
    parent: string;
    siteConfig: Record<string, unknown>;
    applications?: string[];
    language?: string;
    permissions?: ProjectPermission;
    readAccess?: ProjectPermission;
}

interface CreateProjectHandler {
    setId(value: string): void;

    setDisplayName(value: string): void;

    setDescription(value?: string | null): void;

    setLanguage(value?: string | null): void;

    setPermissions(value?: ScriptValue): void;

    setReadAccess(value?: ScriptValue): void;

    setParent(value?: string | null): void;

    setSiteConfig(value?: ScriptValue): void;

    setApplications(value?: string[]): void;

    execute(): Project;
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
 * @param {string} params.parent Parent project id.
 * @param {object} [params.siteConfig] Connected applications config.
 * @param {string[]} [params.applications] - Array of connected applications.
 * @param {Object.<string, string[]>} [params.permissions] Project permissions. 1 to 5 properties where key is role id and value is an array of principals.
 * @param {string} params.permissions.role - Role id (one of `owner`, `editor`, `author`, `contributor`, `viewer`).
 * @param {string[]} params.permissions.principals - Array of principals.
 * @param {Object<string, boolean>} [params.readAccess] Read access settings.
 * @param {boolean} params.readAccess.public Public read access (READ permissions for `system.everyone`).
 *
 * @returns {Object} Created project.
 */
export function create(params: CreateProjectParams): Project {
    checkRequired(params, 'id');
    checkRequired(params, 'displayName');

    const bean = __.newBean<CreateProjectHandler>('com.enonic.xp.lib.project.CreateProjectHandler');
    bean.setId(params.id);
    bean.setDisplayName(params.displayName);
    bean.setDescription(__.nullOrValue(params.description));
    bean.setLanguage(__.nullOrValue(params.language));
    bean.setPermissions(__.toScriptValue(params.permissions));
    bean.setReadAccess(__.toScriptValue(params.readAccess));
    bean.setParent(__.nullOrValue(params.parent));
    bean.setSiteConfig(__.toScriptValue(params.siteConfig));
    bean.setApplications(__.nullOrValue(params.applications));

    return __.toNativeObject(bean.execute());
}

export interface ModifyProjectParams {
    id: string;
    displayName: string;
    description?: string;
    language?: string;
    siteConfig: Record<string, unknown>;
    applications?: string[];
}

interface ModifyProjectHandler {
    setId(value: string): void;

    setDisplayName(value?: string | null): void;

    setDescription(value?: string | null): void;

    setLanguage(value?: string | null): void;

    setSiteConfig(value?: ScriptValue): void;

    setApplications(value?: string[]): void;

    execute(): Project;
}

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
 * @param {object} params.siteConfig Connected applications config.
 * @param {string[]} [params.applications] - Array of connected applications.
 *
 * @returns {Object} Modified project.
 */
export function modify(params: ModifyProjectParams): Project {
    checkRequired(params, 'id');

    const bean = __.newBean<ModifyProjectHandler>('com.enonic.xp.lib.project.ModifyProjectHandler');
    bean.setId(params.id);
    bean.setDisplayName(__.nullOrValue(params.displayName));
    bean.setDescription(__.nullOrValue(params.description));
    bean.setLanguage(__.nullOrValue(params.language));
    bean.setSiteConfig(__.toScriptValue(params.siteConfig));
    bean.setApplications(__.nullOrValue(params.applications));

    return __.toNativeObject(bean.execute());
}

export interface DeleteProjectParams {
    id: string;
}

interface DeleteProjectHandler {
    setId(value: string): void;

    execute(): boolean;
}

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
export function _delete(params: DeleteProjectParams): boolean {
    checkRequired(params, 'id');

    const bean = __.newBean<DeleteProjectHandler>('com.enonic.xp.lib.project.DeleteProjectHandler');

    bean.setId(params.id);

    return __.toNativeObject(bean.execute());
}

export {
    _delete as delete,
};

export interface GetProjectParams {
    id: string;
}

interface GetProjectHandler {
    setId(value: string): void;

    execute(): Project | null;
}

/**
 * Returns an existing Content Project.
 * To `get` a project, user must be a member of one of the project roles, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/get.js
 *
 * @param {GetProjectParams} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 *
 * @returns {Project} Content Project object or `null` if not found.
 */
export function get(params: GetProjectParams): Project | null {
    checkRequired(params, 'id');

    const bean = __.newBean<GetProjectHandler>('com.enonic.xp.lib.project.GetProjectHandler');
    bean.setId(params.id);
    return __.toNativeObject(bean.execute());
}

interface ListProjectsHandler {
    execute(): Project[];
}

/**
 * Returns all Content Projects.
 * The list will be limited to projects that user has permissions for.
 * Users with `system.admin` or `cms.admin` roles will get the list of all projects.
 *
 * @example-ref examples/project/list.js
 *
 * @returns {Project[]} Array of Content Project objects.
 */
export function list(): Project[] {
    const bean = __.newBean<ListProjectsHandler>('com.enonic.xp.lib.project.ListProjectsHandler');
    return __.toNativeObject(bean.execute());
}

export interface AddProjectPermissionsParams {
    id: string;
    permissions?: ProjectPermission;
}

interface AddProjectPermissionsHandler {
    setId(value: string): void;

    setPermissions(value?: ScriptValue): void;

    execute(): ProjectPermissions | null;
}


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
export function addPermissions(params: AddProjectPermissionsParams): ProjectPermissions | null {
    checkRequired(params, 'id');

    const bean = __.newBean<AddProjectPermissionsHandler>('com.enonic.xp.lib.project.AddProjectPermissionsHandler');
    bean.setId(params.id);
    bean.setPermissions(__.toScriptValue(params.permissions));
    return __.toNativeObject(bean.execute());
}

export interface RemoveProjectPermissionsParams {
    id: string;
    permissions?: ProjectPermission;
}

interface RemoveProjectPermissionsHandler {
    setId(value: string): void;

    setPermissions(value?: ScriptValue): void;

    execute(): ProjectPermissions | null;
}

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
export function removePermissions(params: RemoveProjectPermissionsParams): ProjectPermissions | null {
    checkRequired(params, 'id');

    const bean = __.newBean<RemoveProjectPermissionsHandler>('com.enonic.xp.lib.project.RemoveProjectPermissionsHandler');
    bean.setId(params.id);
    bean.setPermissions(__.toScriptValue(params.permissions));

    return __.toNativeObject(bean.execute());
}

export interface ModifyProjectReadAccessParams {
    id: string;
    readAccess?: ProjectReadAccess;
}

interface ModifyProjectReadAccessHandler {
    setId(value: string): void;

    setReadAccess(value?: ScriptValue): void;

    execute(): ProjectReadAccess | null;
}

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
export function modifyReadAccess(params: ModifyProjectReadAccessParams): ProjectReadAccess | null {
    checkRequired(params, 'id');

    const bean = __.newBean<ModifyProjectReadAccessHandler>('com.enonic.xp.lib.project.ModifyProjectReadAccessHandler');
    bean.setId(params.id);
    bean.setReadAccess(__.toScriptValue(params.readAccess));
    return __.toNativeObject(bean.execute());
}
