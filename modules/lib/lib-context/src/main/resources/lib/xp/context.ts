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
    user?: User;
    principals?: PrincipalKey[];
}

export type ContextAttributes = Record<string, number | string | boolean | Record<string, unknown>>;

export interface Context {
    branch?: string;
    repository?: string;
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

    setAttributes(value: ScriptValue | null): void;

    setCallback<T>(fn: () => T): void;
}

export type CustomValue = string | number | boolean | CustomValue[] | {
    [key: string]: CustomValue;
};

interface ContextHandler {
    get(): Context;

    run<T>(params: ContextRunParams): T;

    newRunParams(): ContextRunParams;

    setCustom(name: string, value: ScriptValue | null): void;
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
 * @returns {*} Result of the function execution.
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

/**
 * Stores a JSON-like value as a custom attribute in the local scope of the current context.
 *
 * The local scope lives for the duration of the current execution - for instance a request, including its
 * response processors and filters, or a task run - and is shared with nested `run` calls. This makes it
 * possible to pass data from a page or component controller to response processors and filters.
 *
 * The attribute is stored under the `custom.` prefix and is returned by `get()` as
 * `attributes['custom.<name>']`, similar to how custom events are prefixed.
 *
 * The value is serialized on write: later modifications of the passed object are not reflected in the
 * stored attribute, and readers always get their own copy.
 *
 * Setting `null` or `undefined` removes the attribute.
 *
 * @example
 * contextLib.setCustom('tracking-tags', {tags: ['tag1', 'tag2']});
 * const tags = contextLib.get().attributes['custom.tracking-tags'];
 *
 * @param {string} name Attribute name, stored with the `custom.` prefix.
 * @param {string|number|boolean|array|object|null} [value] JSON-like value to store, or null/undefined to remove the attribute.
 */
export function setCustom(name: string, value?: CustomValue | null): void {
    bean.setCustom(name, value === null || value === undefined ? null : __.toScriptValue(value));
}

