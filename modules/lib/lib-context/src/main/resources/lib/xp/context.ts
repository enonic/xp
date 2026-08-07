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

/**
 * A JSON-like context attribute value.
 */
export type ContextAttributeValue = string | number | boolean | ContextAttributeValue[] | {
    [key: string]: ContextAttributeValue;
};

/**
 * Context attributes, keyed by name.
 *
 * Note what survives a round trip. A value passed to `run` is stored on the context and reaches Java
 * consumers, but `get()` returns only the scalars among them - an array or object set that way is not
 * part of its output. Values stored with `setCustomLocalAttribute` come back from `get()` in full,
 * keyed `custom.<name>`.
 */
export type ContextAttributes = Record<string, ContextAttributeValue>;

export interface Context {
    branch?: string;
    repository?: string;
    authInfo?: AuthInfo;
    /**
     * Attributes visible in the current context: those set on it, those stored in its local scope with
     * `setCustomLocalAttribute` (keyed `custom.<name>`), and any session attributes, merged in that
     * order of precedence.
     */
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

/**
 * Value accepted by {@link setCustomLocalAttribute}.
 */
export type CustomAttributeValue = ContextAttributeValue;

interface ContextHandler {
    get(): Context;

    run<T>(params: ContextRunParams): T;

    newRunParams(): ContextRunParams;

    setCustomLocalAttribute(name: string, value: ScriptValue | null): void;
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
 * The local scope lives for the duration of the current execution and is shared with nested `run` calls,
 * so a value stored by one piece of code is visible to everything else running in the same execution.
 *
 * The attribute is stored under the `custom.` prefix and is returned by `get()` as `attributes['custom.<name>']`.
 *
 * The value is serialized on write: later modifications of the passed object are not reflected in the
 * stored attribute, and readers always get their own copy.
 *
 * Setting `null` or `undefined` removes the attribute.
 *
 * @example
 * contextLib.setCustomLocalAttribute('my-data', {values: ['one', 'two']});
 * const data = contextLib.get().attributes['custom.my-data'];
 *
 * @param {string} name Attribute name, stored with the `custom.` prefix.
 * @param {string|number|boolean|array|object|null} [value] JSON-like value to store, or null/undefined to remove the attribute.
 */
export function setCustomLocalAttribute(name: string, value?: CustomAttributeValue | null): void {
    bean.setCustomLocalAttribute(name, value === null || value === undefined ? null : __.toScriptValue(value));
}

