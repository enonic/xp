import type {ScriptValue} from '@enonic-types/core';

declare global {
    interface XpLibraries {
        '/lib/xp/admin': typeof import('./admin');
    }
}

/**
 * Admin related functions.
 *
 * @example
 * var adminLib = require('/lib/xp/admin');
 *
 * @module admin
 */

function checkRequired<T extends object, K extends keyof T>(
    obj: T,
    name: K,
): NonNullable<T[K]> {
    if (obj == null || obj[name] == null) {
        throw new Error(`Parameter '${String(name)}' is required`);
    }
    return obj[name];
}

const helper: AdminLibHelper = __.newBean<AdminLibHelper>('com.enonic.xp.lib.admin.AdminLibHelper');

interface AdminLibHelper {
    getInstallation(): string;

    getVersion(): string;

    getToolUrl(application: string, tool: string): string;

    getHomeToolUrl(type: string | null): string;
}

interface AdminExtensionUrlHandler {
    setApplication(value: string): void;

    setExtension(value: string): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * Returns the URL for an admin tool of specific application.
 * @param {string} application Full application name (f.ex, 'com.enonic.app')
 * @param {string} tool Name of the tool inside an app (f.ex, 'main')
 *
 * @returns {string} URL.
 */
export function getToolUrl(application: string, tool: string): string {
    return helper.getToolUrl(application, tool);
}

/**
 * Returns the URL for the Home admin tool.
 * @param {object} [params] Parameter object
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 *
 * @returns {string} URL.
 */
export function getHomeToolUrl(params?: GetHomeToolUrlParams): string {
    return helper.getHomeToolUrl(__.nullOrValue(params?.type));
}

export interface GetHomeToolUrlParams {
    type?: HomeToolUrlType;
}

export type HomeToolUrlType = 'server' | 'absolute';

/**
 * Returns installation name.
 *
 * @returns {string} Installation name.
 */
export function getInstallation(): string {
    return helper.getInstallation();
}

/**
 * Returns version of XP installation.
 *
 * @returns {string} Version.
 */
export function getVersion(): string {
    return helper.getVersion();
}

export interface WidgetUrlParams {
    application: string;
    widget: string;
    type?: 'server' | 'absolute';
    params?: object;
}

/**
 * @deprecated Use `extensionUrl` instead. This function will be removed in future versions.
 *
 * Returns the URL for a widget.
 *
 * @param {object} params Parameter object
 * @param {string} params.application Application to reference to a widget.
 * @param {string} params.widget Name of the widget.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} URL.
 */
export function widgetUrl(params: WidgetUrlParams): string {
    const application = checkRequired(params, 'application');
    const widget = checkRequired(params, 'widget');

    const extensionParams: ExtensionUrlParams = {
        application,
        extension: widget,
        type: params.type,
        params: params.params,
    };

    return extensionUrl(extensionParams);
}

export interface ExtensionUrlParams {
    application: string;
    extension: string;
    type?: 'server' | 'absolute';
    params?: object;
}

/**
 * Returns the URL for an extension.
 *
 * @param {object} params Parameter object
 * @param {string} params.application Application to reference to an extension.
 * @param {string} params.extension Name of the extension.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} URL.
 */
export function extensionUrl(params: ExtensionUrlParams): string {
    const application = checkRequired(params, 'application');
    const extension = checkRequired(params, 'extension');

    const bean: AdminExtensionUrlHandler = __.newBean<AdminExtensionUrlHandler>('com.enonic.xp.lib.admin.AdminExtensionUrlHandler');

    bean.setApplication(application);
    bean.setExtension(extension);
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setQueryParams(__.toScriptValue(params.params));

    return bean.createUrl();
}

interface CreateTopicHandler {
    setName(value: string): void;

    setAllow(value: ScriptValue | null): void;

    execute(): string;
}

interface SendToTopicHandler {
    setName(value: string): void;

    setMessage(value: ScriptValue | null): void;

    execute(): void;
}

export interface CreateTopicParams {
    name: string;
    allow?: string[];
}

/**
 * Creates or updates an admin events topic owned by this application.
 *
 * The topic's canonical name is `<application-key>:<name>`. Subscribers address the topic by the
 * canonical name over the `admin:events` websocket API. Only principals listed in `allow` may
 * subscribe; an empty list allows administrators only. Updating re-evaluates current subscribers
 * against the new `allow`. The registration is cleared when the application stops; existing
 * subscriptions persist and resume when the topic is registered again.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Local topic name: 1-255 characters, no `:`, no whitespace.
 * @param {string[]} [params.allow] Principal keys allowed to subscribe. Omitted or empty allows
 * administrators only.
 *
 * @returns {string} The canonical topic name.
 */
export function createTopic(params: CreateTopicParams): string {
    const name = checkRequired(params, 'name');

    const bean: CreateTopicHandler = __.newBean<CreateTopicHandler>('com.enonic.xp.lib.admin.CreateTopicHandler');

    bean.setName(name);
    bean.setAllow(__.toScriptValue(params.allow ?? []));
    return bean.execute();
}

/**
 * Publishes a message to an admin events topic owned by this application.
 *
 * On every cluster node the message is delivered to the sockets holding an acknowledged
 * subscription to the topic, stamped with a per-topic sequence number. Delivery is not
 * guaranteed.
 *
 * @param {string} name Local topic name, as passed to `createTopic`.
 * @param {object} [message] Message data. Must be serializable to JSON.
 */
export function sendToTopic(name: string, message?: object): void {
    if (name == null) {
        throw new Error(`Parameter 'name' is required`);
    }

    const bean: SendToTopicHandler = __.newBean<SendToTopicHandler>('com.enonic.xp.lib.admin.SendToTopicHandler');

    bean.setName(name);
    bean.setMessage(__.toScriptValue(message));
    bean.execute();
}
