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

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw Error(`Parameter '${String(name)}' is required`);
    }
}

const helper: AdminLibHelper = __.newBean<AdminLibHelper>('com.enonic.xp.lib.admin.AdminLibHelper');

interface AdminLibHelper {
    getInstallation(): string;

    getVersion(): string;

    getToolUrl(application: string, tool: string): string;

    getHomeToolUrl(type?: string): string;
}

interface WidgetUrlHandler {
    setApplication(value: string): void;

    setWidget(value: string): void;

    setUrlType(value?: string | null): void;

    addQueryParams(value?: ScriptValue | null): void;

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
    return helper.getHomeToolUrl(params?.type);
}

export interface GetHomeToolUrlParams {
    type: HomeToolUrlType;
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
 * Returns the URL for a widget.
 *
 * @param {object} [params] Parameter object
 * @param {string} params.application Application to reference to a widget.
 * @param {string} params.widget Name of the widget.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} URL.
 */
export function widgetUrl(params: WidgetUrlParams): string {
    checkRequired(params, 'application');
    checkRequired(params, 'widget');

    const bean: WidgetUrlHandler = __.newBean<WidgetUrlHandler>('com.enonic.xp.lib.admin.WidgetUrlHandler');

    bean.setApplication(params.application);
    bean.setWidget(params.widget);
    bean.setUrlType(__.nullOrValue(params.type));
    bean.addQueryParams(__.toScriptValue(params.params));

    return bean.createUrl();
}
