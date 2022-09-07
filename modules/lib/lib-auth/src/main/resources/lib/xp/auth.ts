/**
 * Built-in authentication functions.
 *
 * @example
 * var authLib = require('/lib/xp/auth');
 *
 * @module auth
 */

declare global {
    interface XpLibraries {
        '/lib/xp/auth': typeof import('./auth');
    }
}

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export type EditorFn<T> = (value: T) => T;

export type LoginScopeType = 'SESSION' | 'REQUEST' | 'NONE';

export interface LoginParams {
    user: string;
    skipAuth?: boolean;
    password?: string;
    idProvider?: string;
    scope?: LoginScopeType;
    sessionTimeout?: number;
}

export interface Principal {
    type: string;
    key: string;
    displayName: string;
    modifiedTime: string;
}

export interface User
    extends Principal {
    disabled?: boolean;
    email?: string;
    login?: string;
    idProvider: string;
    profile?: Record<string, unknown>;
}

export interface Role
    extends Principal {
    description?: string;
}

export interface Group
    extends Principal {
    description?: string;
}

export interface LoginResult {
    authenticated: boolean;
    message: string;
    users?: Principal;
}

interface LoginHandler {
    setUser(value: string): void;

    setSkipAuth(value?: boolean): void;

    setPassword(value?: string): void;

    setIdProvider(value?: string[]): void;

    setScope(value?: LoginScopeType): void;

    setSessionTimeout(value?: number | null): void;

    login(): LoginResult;
}

/**
 * Login a user with the specified idProvider, userName, password and scope.
 *
 * @example-ref examples/auth/login.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.user Name of user to log in.
 * @param {string} [params.idProvider] Name of id provider where the user is stored. If not specified it will try all available id providers, in alphabetical order.
 * @param {string} [params.password] Password for the user. Ignored if skipAuth is set to true, mandatory otherwise.
 * @param {('SESSION'|'REQUEST'|'NONE')} [params.scope=SESSION] The scope of this login. Two values are valid. SESSION logs the user in and creates a session in XP for use in future requests. REQUEST logs the user in but only for this particular request and thus does not create a session.
 * @param {boolean} [params.skipAuth=false] Skip authentication.
 * @param {number} [params.sessionTimeout] Session timeout (in seconds). By default, the value of session.timeout from com.enonic.xp.web.jetty.cfg
 * @returns {LoginResult} Information for logged-in user.
 */
export function login(params: LoginParams): LoginResult {
    checkRequired(params, 'user');

    const {
        user,
        skipAuth = false,
        password,
        idProvider,
        scope = 'SESSION' as LoginScopeType,
        sessionTimeout,
    } = params ?? {};

    const bean = __.newBean<LoginHandler>('com.enonic.xp.lib.auth.LoginHandler');

    bean.setUser(user);

    if (params.skipAuth) {
        bean.setSkipAuth(skipAuth);
    } else {
        checkRequired(params, 'password');
        bean.setPassword(password);
    }

    if (idProvider) {
        bean.setIdProvider(([] as string[]).concat(idProvider));
    }

    bean.setScope(scope);

    bean.setSessionTimeout(__.nullOrValue(sessionTimeout));

    return __.toNativeObject(bean.login());
}

interface LogoutHandler {
    logout(): void;
}

/**
 * Logout an already logged-in user.
 *
 * @example-ref examples/auth/logout.js
 */
export function logout(): void {
    const bean = __.newBean<LogoutHandler>('com.enonic.xp.lib.auth.LogoutHandler');

    bean.logout();
}

export interface GetUserParams {
    includeProfile?: boolean;
}

interface GetUserHandler {
    setIncludeProfile(value: boolean): void;

    getUser(): User;
}

/**
 * Returns the logged-in user. If not logged-in, this will return *undefined*.
 *
 * @example-ref examples/auth/getUser.js
 *
 * @param {object} [params] JSON parameters.
 * @param {boolean} [params.includeProfile=false] Include profile.
 *
 * @returns {User} Information for logged-in user.
 */
export function getUser(params ?:GetUserParams): User {
    const {
        includeProfile = false,
    } = params ?? {};

    const bean = __.newBean<GetUserHandler>('com.enonic.xp.lib.auth.GetUserHandler');

    bean.setIncludeProfile(includeProfile);

    return __.toNativeObject(bean.getUser());
}

interface HasRoleHandler {
    setRole(value?: string | null): void;

    hasRole(): boolean;
}

/**
 * Checks if the logged-in user has the specified role.
 *
 * @example-ref examples/auth/hasRole.js
 *
 * @param {string} role Role to check for.
 * @returns {boolean} True if the user has specfied role, false otherwise.
 */
export function hasRole(role: string): boolean {
    const bean = __.newBean<HasRoleHandler>('com.enonic.xp.lib.auth.HasRoleHandler');

    bean.setRole(__.nullOrValue(role));

    return bean.hasRole();
}

interface GeneratePasswordHandler {
    generatePassword(): string;
}

/**
 * Generates a secure password.
 *
 * @example-ref examples/auth/generatePassword.js
 *
 * @returns {string} A secure generated password.
 */
export function generatePassword(): string {
    const bean = __.newBean<GeneratePasswordHandler>('com.enonic.xp.lib.auth.GeneratePasswordHandler');

    return __.toNativeObject(bean.generatePassword());
}

export interface ChangePasswordParams {
    userKey: string;
    password: string;
}

interface ChangePasswordHandler {
    setUserKey(value: string): void;

    setPassword(value: string): void;

    changePassword(): void;
}

/**
 * Changes password for specified user.
 *
 * @example-ref examples/auth/changePassword.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.userKey Key for user to change password.
 * @param {string} params.password New password to set.
 */
export function changePassword(params: ChangePasswordParams): void {
    const bean = __.newBean<ChangePasswordHandler>('com.enonic.xp.lib.auth.ChangePasswordHandler');

    checkRequired(params, 'userKey');
    checkRequired(params, 'password');

    const {userKey, password} = params ?? {};

    bean.setUserKey(userKey);
    bean.setPassword(password);

    bean.changePassword();
}

interface GetPrincipalHandler {
    setPrincipalKey(value?: string | null): void;

    getPrincipal(): User | Group | Role;
}

/**
 * Returns the principal with the specified key.
 *
 * @example-ref examples/auth/getPrincipal.js
 *
 * @param {string} principalKey Principal key to look for.
 * @returns {Principal} the principal specified, or null if it doesn't exist.
 */
export function getPrincipal(principalKey: string): User | Group | Role {
    const bean = __.newBean<GetPrincipalHandler>('com.enonic.xp.lib.auth.GetPrincipalHandler');

    bean.setPrincipalKey(__.nullOrValue(principalKey));

    return __.toNativeObject(bean.getPrincipal());
}

interface GetMembershipsHandler {
    setPrincipalKey(value?: string | null): void;

    setTransitive(value: boolean): void;

    getMemberships(): (User | Group | Role)[];
}

/**
 * Returns a list of principals the specified principal is a member of.
 *
 * @example-ref examples/auth/getMemberships.js
 *
 * @param {string} principalKey Principal key to retrieve memberships for.
 * @param {boolean} [transitive=false] Retrieve transitive memberships.
 * @returns {Array.<User | Group | Role>} Returns the list of principals.
 */
export function getMemberships(principalKey: string, transitive: boolean): (User | Group | Role)[] {
    const bean = __.newBean<GetMembershipsHandler>('com.enonic.xp.lib.auth.GetMembershipsHandler');

    bean.setPrincipalKey(__.nullOrValue(principalKey));
    bean.setTransitive(transitive);

    return __.toNativeObject(bean.getMemberships());
}

interface GetMembersHandler {
    setPrincipalKey(value?: string | null): void;

    getMembers(): (User | Group | Role)[];
}

/**
 * Returns a list of principals that are members of the specified principal.
 *
 * @example-ref examples/auth/getMembers.js
 *
 * @param {string} principalKey Principal key to retrieve members for.
 * @returns {Array.<User | Group | Role>} Returns the list of principals.
 */
export function getMembers(principalKey: string): (User | Group | Role)[] {
    const bean = __.newBean<GetMembersHandler>('com.enonic.xp.lib.auth.GetMembersHandler');

    bean.setPrincipalKey(__.nullOrValue(principalKey));

    return __.toNativeObject(bean.getMembers());
}

export interface CreateUserParams {
    name: string;
    displayName: string;
    idProvider: string;
    email?: string;
}

interface CreateUserHandler {
    setIdProvider(value: string): void;

    setName(value: string): void;

    setDisplayName(value?: string | null): void;

    setEmail(value?: string | null): void;

    createUser(): User | null;
}

/**
 * Creates user from passed parameters.
 *
 * @example-ref examples/auth/createUser.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.idProvider Key for id provider where user has to be created.
 * @param {string} params.name User login name to set.
 * @param {string} params.displayName User display name.
 * @param {string} [params.email] User email.
 */
export function createUser(params: CreateUserParams): User | null {
    checkRequired(params, 'name');
    checkRequired(params, 'idProvider');

    const bean = __.newBean<CreateUserHandler>('com.enonic.xp.lib.auth.CreateUserHandler');

    bean.setIdProvider(params.idProvider);
    bean.setName(params.name);
    bean.setDisplayName(__.nullOrValue(params.displayName));
    bean.setEmail(__.nullOrValue(params.email));

    return __.toNativeObject(bean.createUser());
}

export interface ModifyUserParams {
    key: string;
    editor: EditorFn<User>;
}

interface ModifyUserHandler {
    setPrincipalKey(value: string): void;

    setEditor(value: ScriptValue): void;

    modifyUser(): User | null;
}

/**
 * Retrieves the user specified and updates it with the changes applied.
 *
 * @example-ref examples/auth/modifyUser.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the user to modify.
 * @param {function} params.editor User editor function to apply to user.
 * @returns {User} the updated user or null if a  user not found.
 */
export function modifyUser(params: ModifyUserParams): User | null {
    checkRequired(params, 'key');
    checkRequired(params, 'editor');

    const bean = __.newBean<ModifyUserHandler>('com.enonic.xp.lib.auth.ModifyUserHandler');

    bean.setPrincipalKey(params.key);
    bean.setEditor(__.toScriptValue(params.editor));

    return __.toNativeObject(bean.modifyUser());
}

export interface CreateGroupParams {
    idProvider: string;
    name: string;
    displayName: string;
    description: string;
}

interface CreateGroupHandler {
    setIdProvider(value: string): void;

    setName(value: string): void;

    setDisplayName(value?: string | null): void;

    setDescription(value?: string | null): void;

    createGroup(): Group | null;
}

/**
 * Creates a group.
 *
 * @example-ref examples/auth/createGroup.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.idProvider Key for id provider where group has to be created.
 * @param {string} params.name Group name.
 * @param {string} params.displayName Group display name.
 * @param {string} params.description as principal description .
 */
export function createGroup(params: CreateGroupParams): Group | null {
    checkRequired(params, 'idProvider');
    checkRequired(params, 'name');

    const bean = __.newBean<CreateGroupHandler>('com.enonic.xp.lib.auth.CreateGroupHandler');

    bean.setIdProvider(params.idProvider);
    bean.setName(params.name);
    bean.setDisplayName(__.nullOrValue(params.displayName));
    bean.setDescription(__.nullOrValue(params.description));

    return __.toNativeObject(bean.createGroup());
}

export interface ModifyGroupParams {
    key: string;
    editor: EditorFn<Group>;
}

interface ModifyGroupHandler {
    setPrincipalKey(value: string): void;

    setEditor(value: ScriptValue): void;

    modifyGroup(): Group | null;
}

/**
 * Retrieves the group specified and updates it with the changes applied.
 *
 * @example-ref examples/auth/modifyGroup.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the group to modify.
 * @param {function} params.editor Group editor function to apply to group.
 * @returns {Group} the updated group or null if a group not found.
 */
export function modifyGroup(params: ModifyGroupParams): Group | null {
    checkRequired(params, 'key');
    checkRequired(params, 'editor');

    const bean = __.newBean<ModifyGroupHandler>('com.enonic.xp.lib.auth.ModifyGroupHandler');

    bean.setPrincipalKey(params.key);
    bean.setEditor(__.toScriptValue(params.editor));

    return __.toNativeObject(bean.modifyGroup());
}

interface AddMembersHandler {
    setPrincipalKey(value?: string | null): void;

    setMembers(value: string[]): void;

    addMembers(): void;
}

/**
 * Adds members to a principal (user or role).
 *
 * @example-ref examples/auth/addMembers.js
 *
 * @param {string} principalKey Key of the principal to add members to.
 * @param {string} members Keys of the principals to add.
 */
export function addMembers(principalKey: string, members: string): void {
    const bean = __.newBean<AddMembersHandler>('com.enonic.xp.lib.auth.AddMembersHandler');

    bean.setPrincipalKey(__.nullOrValue(principalKey));
    bean.setMembers(([] as string[]).concat(members));

    __.toNativeObject(bean.addMembers());
}

interface RemoveMembersHandler {
    setPrincipalKey(value?: string | null): void;

    setMembers(value: string[]): void;

    removeMembers(): void;
}

/**
 * Removes members from a principal (user or role).
 *
 * @example-ref examples/auth/removeMembers.js
 *
 * @param {string} principalKey Key of the principal to remove members from.
 * @param {string} members Keys of the principals to remove.
 */
export function removeMembers(principalKey: string, members: string): void {
    const bean = __.newBean<RemoveMembersHandler>('com.enonic.xp.lib.auth.RemoveMembersHandler');

    bean.setPrincipalKey(__.nullOrValue(principalKey));
    bean.setMembers(([] as string[]).concat(members));

    __.toNativeObject(bean.removeMembers());
}

export interface FindPrincipalsParams {
    type?: string;
    idProvider?: string;
    start?: number;
    count?: number;
    name?: string;
    searchText?: string;
}

export interface FindPrincipalsResult {
    total: number;
    count: number;
    hits: (User | Group | Role)[];
}

interface FindPrincipalsHandler {
    setType(value?: string | null): void;

    setIdProvider(value?: string | null): void;

    setStart(value?: number | null): void;

    setCount(value?: number | null): void;

    setName(value?: string | null): void;

    setSearchText(value?: string | null): void;

    findPrincipals(): FindPrincipalsResult;
}

/**
 * Search for principals matching the specified criteria.
 *
 * @example-ref examples/auth/findPrincipals.js
 *
 * @param {object} params JSON parameters.
 * @param {string} [params.type] Principal type to look for, one of: 'user', 'group' or 'role'. If not specified all principal types will be included.
 * @param {string} [params.idProvider] Key of the id provider to look for. If not specified all id providers will be included.
 * @param {number} [params.start=0] First principal to return from the search results. It can be used for pagination.
 * @param {number} [params.count=10] A limit on the number of principals to be returned.
 * @param {string} [params.name] Name of the principal to look for.
 * @param {string} [params.searchText] Text to look for in any principal field.
 * @returns {FindPrincipalsResult} The "total" number of principals matching the search, the "count" of principals included, and an array of "hits" containing the principals.
 */
export function findPrincipals(params: FindPrincipalsParams): FindPrincipalsResult {
    const bean = __.newBean<FindPrincipalsHandler>('com.enonic.xp.lib.auth.FindPrincipalsHandler');

    const {
        type,
        idProvider,
        start = 0,
        count = 10,
        name,
        searchText,
    } = params ?? {};

    bean.setType(__.nullOrValue(type));
    bean.setIdProvider(__.nullOrValue(idProvider));
    bean.setStart(__.nullOrValue(start));
    bean.setCount(__.nullOrValue(count));
    bean.setName(__.nullOrValue(name));
    bean.setSearchText(__.nullOrValue(searchText));

    return __.toNativeObject(bean.findPrincipals());
}

interface DeletePrincipalHandler {
    setPrincipalKey(value?: string | null): void;

    deletePrincipal(): boolean;
}

/**
 * Deletes the principal with the specified key.
 *
 * @example-ref examples/auth/deletePrincipal.js
 *
 * @param {string} principalKey Principal key to delete.
 * @returns {boolean} True if deleted, false otherwise.
 */
export function deletePrincipal(principalKey: string): boolean {
    const bean = __.newBean<DeletePrincipalHandler>('com.enonic.xp.lib.auth.DeletePrincipalHandler');
    bean.setPrincipalKey(__.nullOrValue(principalKey));
    return __.toNativeObject(bean.deletePrincipal());
}

interface GetIdProviderConfigHandler {
    execute(): Record<string, unknown>;
}

/**
 * This function returns the ID provider configuration.
 * It is meant to be called from an ID provider controller.
 *
 * @example-ref examples/auth/getIdProviderConfig.js
 *
 * @returns {object} The ID provider configuration as JSON.
 */
export function getIdProviderConfig(): Record<string, unknown> {
    const bean = __.newBean<GetIdProviderConfigHandler>('com.enonic.xp.lib.auth.GetIdProviderConfigHandler');
    return __.toNativeObject(bean.execute());
}

export interface GetProfileParams {
    key: string;
    scope?: string;
}

interface GetProfileHandler {
    setKey(value: string): void;

    setScope(value?: string | null): void;

    execute(): Record<string, unknown>;
}

/**
 * This function retrieves the profile of a user.
 *
 * @example-ref examples/auth/getProfile.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the user.
 * @param {string} [params.scope] Scope of the data to retrieve.
 * @returns {object} The extra data as JSON
 */
export function getProfile(params: GetProfileParams): Record<string, unknown> {
    checkRequired(params, 'key');

    const bean = __.newBean<GetProfileHandler>('com.enonic.xp.lib.auth.GetProfileHandler');

    bean.setKey(params.key);
    bean.setScope(__.nullOrValue(params.scope));

    return __.toNativeObject(bean.execute());
}

export interface ModifyProfileParams {
    key: string;
    scope?: string | null;
    editor: EditorFn<User>;
}

interface ModifyProfileHandler {
    setKey(value: string): void;

    setScope(value?: string | null): void;

    setEditor(value: ScriptValue): void;

    execute(): Record<string, unknown> | null;
}

/**
 * This function retrieves the profile of a user and updates it.
 *
 * @example-ref examples/auth/modifyProfile.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the user.
 * @param {string} [params.scope] Scope of the data to retrieve and update.
 * @param {function} params.editor Profile editor function to apply.
 * @returns {object} The extra data as JSON
 */
export function modifyProfile(params: ModifyProfileParams): Record<string, unknown> | null {
    checkRequired(params, 'key');
    checkRequired(params, 'editor');

    const bean = __.newBean<ModifyProfileHandler>('com.enonic.xp.lib.auth.ModifyProfileHandler');

    bean.setKey(params.key);
    bean.setScope(__.nullOrValue(params.scope));
    bean.setEditor(__.toScriptValue(params.editor));

    return __.toNativeObject(bean.execute());
}

export interface FindUsersParams {
    start?: number;
    count?: number;
    query: string;
    sort?: string;
    includeProfile?: boolean;
}

interface FindUsersHandler {
    setStart(value: number): void;

    setCount(value: number): void;

    setQuery(value?: string | null): void;

    setSort(value?: string | null): void;

    setIncludeProfile(value: boolean): void;

    execute(): FindPrincipalsResult;
}

/**
 * Search for users matching the specified query.
 *
 * @example-ref examples/auth/findUsers.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {string} [params.sort] Sorting expression.
 * @param {boolean} [params.includeProfile=false] Include profile.
 *
 * @returns {FindPrincipalsResult} Result of query.
 */
export function findUsers(params: FindUsersParams): FindPrincipalsResult {
    const {
        start = 0,
        count = 10,
        query,
        sort,
        includeProfile = false,
    } = params ?? {};

    const bean = __.newBean<FindUsersHandler>('com.enonic.xp.lib.auth.FindUsersHandler');

    bean.setStart(start);
    bean.setCount(count);
    bean.setQuery(__.nullOrValue(query));
    bean.setSort(__.nullOrValue(sort));
    bean.setIncludeProfile(includeProfile);
    return __.toNativeObject(bean.execute());
}

export interface CreateRoleParams {
    name: string;
    displayName: string;
    description: string;
}

interface CreateRoleHandler {
    setName(value: string): void;

    setDisplayName(value?: string | null): void;

    setDescription(value?: string | null): void;

    createRole(): Role;
}

/**
 * Creates a role.
 *
 * @example-ref examples/auth/createRole.js
 *
 * @param {string} params.name Role name.
 * @param {string} [params.displayName] Role display name.
 * @param {string} [params.description] as principal description .
 */
export function createRole(params: CreateRoleParams): Role {
    checkRequired(params, 'name');

    const bean = __.newBean<CreateRoleHandler>('com.enonic.xp.lib.auth.CreateRoleHandler');

    bean.setName(params.name);
    bean.setDisplayName(__.nullOrValue(params.displayName));
    bean.setDescription(__.nullOrValue(params.description));

    return __.toNativeObject(bean.createRole());
}

export interface ModifyRoleParams {
    key: string;
    editor: EditorFn<Role>;
}

interface ModifyRoleHandler {
    setPrincipalKey(value: string): void;

    setEditor(value: ScriptValue): void;

    modifyRole(): Role | null;
}

/**
 * Retrieves the role specified and updates it with the changes applied.
 *
 * @example-ref examples/auth/modifyRole.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Principal key of the role to modify.
 * @param {function} params.editor Role editor function to apply to role.
 * @returns {Role} the updated role or null if a role not found.
 */
export function modifyRole(params: ModifyRoleParams): Role | null {
    checkRequired(params, 'key');
    checkRequired(params, 'editor');

    const bean = __.newBean<ModifyRoleHandler>('com.enonic.xp.lib.auth.ModifyRoleHandler');

    bean.setPrincipalKey(params.key);
    bean.setEditor(__.toScriptValue(params.editor));

    return __.toNativeObject(bean.modifyRole());
}
