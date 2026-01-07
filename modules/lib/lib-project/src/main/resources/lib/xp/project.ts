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

import type {ScriptValue} from '@enonic-types/core';

export type {ScriptValue} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw Error(`Parameter '${String(name)}' is required`);
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

export interface SiteConfig<Config> {
    applicationKey: string;
    config?: Config;
}

export interface CreateProjectParams<Config extends Record<string, unknown>> {
    id: string;
    displayName: string;
    description?: string;
    language?: string;
    parent?: string;
    parents?: string[];
    siteConfig?: SiteConfig<Config>[];
    permissions?: ProjectPermission;
    readAccess: ProjectReadAccess;
}

export interface Project<Config extends Record<string, unknown> = Record<string, unknown>> {
    id: string;
    displayName: string;
    description: string;
    parent?: string;
    parents: string[];
    siteConfig: SiteConfig<Config>[];
    language?: string;
    permissions?: ProjectPermission;
    readAccess?: ProjectPermission;
}

interface CreateProjectHandler<Config extends Record<string, unknown>> {
    setId(value: string): void;

    setDisplayName(value: string): void;

    setDescription(value?: string | null): void;

    setLanguage(value?: string | null): void;

    setPermissions(value?: ScriptValue): void;

    setReadAccess(value?: ScriptValue): void;

    setParent(value?: string | null): void;

    setParents(value?: string[] | null): void;

    setSiteConfig(value?: ScriptValue): void;

    execute(): Project<Config>;
}

/**
 * Creates a new Content Project. Only `system.admin` and `cms.admin` roles have permissions to create new projects.
 *
 * @example-ref examples/project/create.js
 *
 * @param {Object} params JSON with the parameters.
 * @param {string} params.id Unique project id (alphanumeric characters and hyphens allowed).
 * @param {string} params.displayName Project's display name.
 * @param {string} [params.description] Project description.
 * @param {string} [params.language] Default project language.
 * @param {string} [params.parent] Deprecated: use 'parents' param. Parent project id.
 * @param {string[]} [params.parents] Parent project ids.
 * @param {object} [params.siteConfig] Connected applications config.
 * @param {Object.<string, string[]>} [params.permissions] Project permissions. 1 to 5 properties where key is role id and value is an array of principals.
 * @param {string} params.permissions.role - Role id (one of `owner`, `editor`, `author`, `contributor`, `viewer`).
 * @param {string[]} params.permissions.principals - Array of principals.
 * @param {Object<string, boolean>} [params.readAccess] Read access settings.
 * @param {boolean} params.readAccess.public Public read access (READ permissions for `system.everyone`).
 *
 * @returns {Object} Created project.
 */
export function create<Config extends Record<string, unknown> = Record<string, unknown>>(params: CreateProjectParams<Config>): Project<Config> {
    checkRequired(params, 'id');
    checkRequired(params, 'displayName');

    const bean: CreateProjectHandler<Config> = __.newBean<CreateProjectHandler<Config>>('com.enonic.xp.lib.project.CreateProjectHandler');
    bean.setId(params.id);
    bean.setDisplayName(params.displayName);
    bean.setDescription(__.nullOrValue(params.description));
    bean.setLanguage(__.nullOrValue(params.language));
    bean.setPermissions(__.toScriptValue(params.permissions));
    bean.setReadAccess(__.toScriptValue(params.readAccess));
    if (params.parent != null) {
        bean.setParents([params.parent]);
    }
    if (params.parents) {
        bean.setParents(__.nullOrValue(params.parents));
    }
    bean.setSiteConfig(__.toScriptValue(params.siteConfig));

    return __.toNativeObject(bean.execute());
}

export interface ModifyProjectParams<Config extends Record<string, unknown>> {
    id: string;
    displayName: string;
    description?: string;
    language?: string;
    siteConfig?: SiteConfig<Config>[];
}

interface ModifyProjectHandler<Config extends Record<string, unknown>> {
    setId(value: string): void;

    setDisplayName(value?: string | null): void;

    setDescription(value?: string | null): void;

    setLanguage(value?: string | null): void;

    setSiteConfig(value?: ScriptValue): void;

    execute(): Project<Config>;
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
 * @param {object} [params.siteConfig] Connected applications config.
 *
 * @returns {Object} Modified project.
 */
export function modify<Config extends Record<string, unknown> = Record<string, unknown>>(params: ModifyProjectParams<Config>): Project<Config> {
    checkRequired(params, 'id');

    const bean: ModifyProjectHandler<Config> = __.newBean<ModifyProjectHandler<Config>>('com.enonic.xp.lib.project.ModifyProjectHandler');
    bean.setId(params.id);
    bean.setDisplayName(__.nullOrValue(params.displayName));
    bean.setDescription(__.nullOrValue(params.description));
    bean.setLanguage(__.nullOrValue(params.language));
    bean.setSiteConfig(__.toScriptValue(params.siteConfig));

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
export function del(params: DeleteProjectParams): boolean {
    checkRequired(params, 'id');

    const bean: DeleteProjectHandler = __.newBean<DeleteProjectHandler>('com.enonic.xp.lib.project.DeleteProjectHandler');

    bean.setId(params.id);

    return __.toNativeObject(bean.execute());
}

export {
    del as delete,
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

    const bean: GetProjectHandler = __.newBean<GetProjectHandler>('com.enonic.xp.lib.project.GetProjectHandler');
    bean.setId(params.id);
    return __.toNativeObject(bean.execute());
}


export interface GetAvailableApplicationsParams {
    id: string;
}

interface GetAvailableApplicationsHandler {
    setId(value: string): void;

    execute(): string[];
}

/**
 * Returns available applications for the specified project. It contains apps specified for the current and all parent projects/layers.
 * User must be a member of one of the project roles, or either `system.admin` or `cms.admin` role.
 *
 * @example-ref examples/project/getAvailableApplications.js
 *
 * @param {GetProjectParams} params JSON with the parameters.
 * @param {string} params.id Unique project id to identify the project.
 *
 * @returns {string[]} Keys of the available applications.
 */
export function getAvailableApplications(params: GetAvailableApplicationsParams): string[] {
    checkRequired(params, 'id');

    const bean: GetAvailableApplicationsHandler = __.newBean<GetAvailableApplicationsHandler>('com.enonic.xp.lib.project.GetAvailableApplicationsHandler');
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
    const bean: ListProjectsHandler = __.newBean<ListProjectsHandler>('com.enonic.xp.lib.project.ListProjectsHandler');
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

    const bean: AddProjectPermissionsHandler = __.newBean<AddProjectPermissionsHandler>('com.enonic.xp.lib.project.AddProjectPermissionsHandler');
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

    const bean: RemoveProjectPermissionsHandler = __.newBean<RemoveProjectPermissionsHandler>('com.enonic.xp.lib.project.RemoveProjectPermissionsHandler');
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
 * This will modify permissions on ALL the content items inside the project repository by adding or removing READ access for `system.everyone`.
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

    const bean: ModifyProjectReadAccessHandler = __.newBean<ModifyProjectReadAccessHandler>('com.enonic.xp.lib.project.ModifyProjectReadAccessHandler');
    bean.setId(params.id);
    bean.setReadAccess(__.toScriptValue(params.readAccess));
    return __.toNativeObject(bean.execute());
}
