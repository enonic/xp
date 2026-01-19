/**
 * Built-in context functions.
 *
 * @example
 * var contextLib = require('/lib/xp/context');
 *
 * @module context
 */

declare global {
    interface XpLibraries {
        '/lib/xp/context': typeof import('./context');
    }
}

import type {PrincipalKey, ScriptValue, User} from '@enonic-types/core';

export type {PrincipalKey, UserKey, Principal, ScriptValue, User} from '@enonic-types/core';

export interface AuthInfo {
    user?: User | null;
    principals?: PrincipalKey[] | null;
}

export type ContextAttributes = Record<string, number | string | boolean | Record<string, unknown>>;

export interface Context {
    branch: string;
    repository: string;
    authInfo?: AuthInfo;
    attributes: ContextAttributes;
}

export interface ContextUserParams {
    login: string;
    idProvider?: string;
}

export interface ContextParams {
    repository?: string;
    branch?: string;
    user?: ContextUserParams;
    principals?: PrincipalKey[];
    attributes?: ContextAttributes;
}

interface ContextRunParams {
    setRepository(value: string): void;

    setBranch(value: string): void;

    setUsername(value: string): void;

    setIdProvider(value: string): void;

    setPrincipals(value: PrincipalKey[]): void;

    setAttributes(value: ScriptValue): void;

    setCallback<T>(fn: () => T): void;
}

interface ContextHandler {
    get(): Context;

    run<T>(params: ContextRunParams): T;

    newRunParams(): ContextRunParams;
}

const bean: ContextHandler = __.newBean<ContextHandler>('com.enonic.xp.lib.context.ContextHandlerBean');

/**
 * Runs a function within a specified context.
 *
 * @example-ref examples/context/run.js
 *
 * @param {object} context JSON parameters.
 * @param {string} [context.repository] Repository to execute the callback in. Default is the current repository set in portal.
 * @param {string} [context.branch] Name of the branch to execute the callback in. Default is the current branch set in portal.
 * @param {object} [context.user] User to execute the callback with. Default is the current user.
 * @param {string} context.user.login Login of the user.
 * @param {string} [context.user.idProvider] Id provider containing the user. By default, the system id provider will be used.
 * @param {array} [context.principals] Additional principals to execute the callback with.
 * @param {object} [context.attributes] Additional Context attributes.
 * @param {function} callback Function to execute.
 * @returns {object} Result of the function execution.
 */
export function run<T>(context: ContextParams, callback: () => T): T {
    const params: ContextRunParams = bean.newRunParams();
    params.setCallback(callback);

    if (context.repository) {
        params.setRepository(context.repository);
    }

    if (context.branch) {
        params.setBranch(context.branch);
    }

    if (context.user) {
        if (context.user.login) {
            params.setUsername(context.user.login);
        }
        if (context.user.idProvider) {
            params.setIdProvider(context.user.idProvider);
        }
    }

    if (context.principals) {
        params.setPrincipals(context.principals);
    }
    if (context.attributes) {
        params.setAttributes(__.toScriptValue(context.attributes));
    }

    return __.toNativeObject(bean.run(params));
}

/**
 * Returns the current context.
 *
 * @example-ref examples/context/get.js
 *
 * @returns {object} Return the current context as JSON object.
 */
export function get(): Context {
    const result = bean.get();
    return __.toNativeObject(result);
}

