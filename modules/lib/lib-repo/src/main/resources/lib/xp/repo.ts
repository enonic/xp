/**
 * Node repository related functions.
 *
 * @example
 * var repoLib = require('/lib/xp/repo');
 *
 * @module repo
 */

declare global {
    interface XpLibraries {
        '/lib/xp/repo': typeof import('./repo');
    }
}

import type {ScriptValue} from '@enonic-types/core';

export type {ScriptValue} from '@enonic-types/core';

function checkRequiredValue(value: unknown, name: string): void {
    if (value == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export interface IndexDefinition {
    settings?: Record<string, unknown>; // https://www.elastic.co/guide/en/elasticsearch/reference/2.4/index-modules.html
    mapping?: Record<string, unknown>;
}

export interface RepositorySettings {
    definitions?: {
        SEARCH?: IndexDefinition,
        VERSION?: IndexDefinition,
        BRANCH?: IndexDefinition,
        COMMIT?: IndexDefinition,
    }
}

export interface Repository {
    id: string;
    branches: string[];
    settings?: RepositorySettings;
    data?: Record<string, unknown>;
}

export type RepoRefreshType = 'all' | 'search' | 'storage';

export interface RefreshParams {
    mode?: RepoRefreshType;
    repo?: string;
    branch?: string;
}

interface RefreshHandler {
    setMode(value?: RepoRefreshType | null): void;

    setRepoId(value?: string | null): void;

    setBranch(value?: string | null): void;

    refresh(): void;
}

/**
 * Refresh the data for the given index-type in the current repository.
 *
 * @example-ref examples/repo/refresh.js
 *
 * @param {object?} params JSON with the parameters.
 * @param {string} [params.mode='all'] Index type to be refreshed. Possible values: 'all' | 'search' | 'storage'.
 * @param {string} [params.repo='com.enonic.cms.default'] Repository id: 'com.enonic.cms.default' | 'system-repo'. Default is the current repository set in portal.
 * @param {string} [params.branch='master'] Branch. Default is the current repository set in portal.
 *
 */
export function refresh(params: RefreshParams): void {
    const {
        mode = 'all',
        repo,
        branch,
    } = params ?? {};

    const bean: RefreshHandler = __.newBean<RefreshHandler>('com.enonic.xp.lib.repo.RefreshHandler');

    bean.setMode(__.nullOrValue(mode));
    bean.setRepoId(__.nullOrValue(repo));
    bean.setBranch(__.nullOrValue(branch));

    bean.refresh();
}

export type Permission = 'READ' | 'CREATE' | 'MODIFY' | 'DELETE' | 'PUBLISH' | 'READ_PERMISSIONS' | 'WRITE_PERMISSIONS';

export interface AccessControlEntry {
    principal: string;
    allow?: Permission[];
    deny?: Permission[];
}

export interface CreateRepositoryParams {
    id: string;
    rootPermissions?: AccessControlEntry[];
    rootChildOrder?: string;
    settings?: RepositorySettings
}

interface CreateRepositoryHandler {
    setRepositoryId(value: string): void;

    setRootPermissions(value: ScriptValue): void;

    setRootChildOrder(value?: string | null): void;

    setIndexDefinitions(value: ScriptValue): void;

    execute(): Repository;
}

/**
 @typedef IndexDefinition
 @type {object}
 @property {object} [settings] - Index definition settings.
 @property {object} [mapping] - Index definition settings.
 */
/**
 * Creates a repository
 *
 * @example-ref examples/repo/create.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Repository ID.
 * @param {array} [params.rootPermissions] Array of root permissions.
 * By default, all permissions to 'system.admin' and read permission to 'system.authenticated'
 * @param {string} [params.rootChildOrder] Root child order.
 * @param {object} [params.settings] Repository settings.
 * @param {object} [params.settings.definitions] Index definitions.
 * @param {IndexDefinition} [params.settings.definitions.search] Search index definition.
 * @param {IndexDefinition} [params.settings.definitions.version] Version index definition.
 * @param {IndexDefinition} [params.settings.definitions.branch] Branch indexes definition.
 *
 * @returns {object} Repository created as JSON.
 *
 */
export function create(params: CreateRepositoryParams): Repository {
    checkRequired(params, 'id');

    const bean: CreateRepositoryHandler = __.newBean<CreateRepositoryHandler>('com.enonic.xp.lib.repo.CreateRepositoryHandler');

    bean.setRepositoryId(params.id);
    bean.setRootChildOrder(__.nullOrValue(params.rootChildOrder));
    if (params.settings && params.settings.definitions) {
        bean.setIndexDefinitions(__.toScriptValue(params.settings.definitions));
    }
    if (params.rootPermissions) {
        bean.setRootPermissions(__.toScriptValue(params.rootPermissions));
    }

    return __.toNativeObject(bean.execute());
}

interface DeleteRepositoryHandler {
    setRepositoryId(value: string): void;

    execute(): boolean;
}

/**
 * Deletes a repository
 *
 * @example-ref examples/repo/delete.js
 *
 * @param {string} id Repository ID.
 * @return {boolean} true if deleted, false otherwise.
 *
 */
function _delete(id: string): boolean {
    checkRequiredValue(id, 'id');
    const bean: DeleteRepositoryHandler = __.newBean<DeleteRepositoryHandler>('com.enonic.xp.lib.repo.DeleteRepositoryHandler');
    bean.setRepositoryId(id);
    return bean.execute();
}

export {
    _delete as delete,
};

interface ListRepositoriesHandler {
    execute(): Repository[];
}

/**
 * Retrieves the list of repositories
 *
 * @example-ref examples/repo/list.js
 * @return {object} The repositories (as JSON array).
 *
 */
export function list(): Repository[] {
    const bean: ListRepositoriesHandler = __.newBean<ListRepositoriesHandler>('com.enonic.xp.lib.repo.ListRepositoriesHandler');
    return __.toNativeObject(bean.execute());
}

interface GetRepositoryHandler {
    setRepositoryId(value: string): void;

    execute(): Repository | null;
}

/**
 * Retrieves a repository
 *
 * @example-ref examples/repo/get.js
 *
 * @param {string} id Repository ID.
 * @return {object} The repository (as JSON).
 *
 */
export function get(id: string): Repository | null {
    checkRequiredValue(id, 'id');

    const bean: GetRepositoryHandler = __.newBean<GetRepositoryHandler>('com.enonic.xp.lib.repo.GetRepositoryHandler');
    bean.setRepositoryId(id);
    return __.toNativeObject(bean.execute());
}

export interface CreateBranchParams {
    branchId: string;
    repoId: string;
}

export interface BranchResult {
    id: string;
}

interface CreateBranchHandler {
    setBranchId(value: string): void;

    setRepoId(value: string): void;

    execute(): BranchResult;
}

/**
 * Creates a branch
 *
 * @example-ref examples/repo/createBranch.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.branchId Branch ID.
 * @param {string} params.repoId Repository where the branch should be created.
 * @return {object} The branch (as JSON).
 *
 */
export function createBranch(params: CreateBranchParams): BranchResult {
    checkRequired(params, 'repoId');
    checkRequired(params, 'branchId');

    const bean: CreateBranchHandler = __.newBean<CreateBranchHandler>('com.enonic.xp.lib.repo.CreateBranchHandler');

    bean.setBranchId(params.branchId);
    bean.setRepoId(params.repoId);

    return __.toNativeObject(bean.execute());
}

export interface DeleteBranchParams {
    branchId: string;
    repoId: string;
}

interface DeleteBranchHandler {
    setBranchId(value: string): void;

    setRepoId(value: string): void;

    execute(): BranchResult;
}

/**
 * Deletes a branch
 *
 * @example-ref examples/repo/deleteBranch.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.branchId Branch ID.
 * @param {string} params.repoId Repository where the branch should be deleted.
 * @return {object} The branch (as JSON).
 *
 */
export function deleteBranch(params: DeleteBranchParams): BranchResult {
    checkRequired(params, 'repoId');
    checkRequired(params, 'branchId');

    const bean: DeleteBranchHandler = __.newBean<DeleteBranchHandler>('com.enonic.xp.lib.repo.DeleteBranchHandler');
    bean.setBranchId(params.branchId);
    bean.setRepoId(params.repoId);
    return __.toNativeObject(bean.execute());
}

export type EditorFn<T> = (value: T) => T;

export interface ModifyRepositoryParams {
    id: string;
    editor: EditorFn<Repository>;
    scope?: string | null;
}

interface ModifyRepositoryHandler {
    setId(value: string): void;

    setEditor(value: ScriptValue): void;

    setScope(value?: string | null): void;

    execute(): Repository;
}

/**
 * Updates a repository
 *
 * @example-ref examples/repo/modify.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Repository ID.
 * @param {string} [params.scope] Scope of the data to retrieve and update.
 * @param {function} params.editor Editor callback function.
 *
 * @returns {object} Repository updated as JSON.
 *
 */
export function modify(params: ModifyRepositoryParams): Repository {
    checkRequired(params, 'id');
    checkRequired(params, 'editor');

    const bean: ModifyRepositoryHandler = __.newBean<ModifyRepositoryHandler>('com.enonic.xp.lib.repo.ModifyRepositoryHandler');

    bean.setId(params.id);
    bean.setEditor(__.toScriptValue(params.editor));
    bean.setScope(__.nullOrValue(params.scope));

    return __.toNativeObject(bean.execute());
}

export interface GetRepositoryBinaryParams {
    repoId: string;
    binaryReference: string;
}

interface GetRepositoryBinaryHandler {
    setRepositoryId(value: string): void;

    setBinaryReference(value: string): void;

    execute(): object;
}

/**
 * This function returns a data-stream for the specified repository attachment.
 *
 * @example-ref examples/repo/getBinary.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.repoId Repository ID.
 * @param {string} params.binaryReference to the binary.
 *
 * @returns {*} Stream of the attachment data.
 */
export function getBinary(params: GetRepositoryBinaryParams): object {
    checkRequired(params, 'repoId');
    checkRequired(params, 'binaryReference');

    const bean: GetRepositoryBinaryHandler = __.newBean<GetRepositoryBinaryHandler>('com.enonic.xp.lib.repo.GetRepositoryBinaryHandler');

    bean.setRepositoryId(params.repoId);
    bean.setBinaryReference(params.binaryReference);

    return bean.execute();
}
