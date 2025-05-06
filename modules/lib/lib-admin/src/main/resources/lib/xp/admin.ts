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


const portal = require('/lib/xp/portal');

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

const helper: AdminLibHelper = __.newBean<AdminLibHelper>('com.enonic.xp.lib.admin.AdminLibHelper');

interface AdminLibHelper {
    getInstallation(): string;

    getVersion(): string;
}

/**
 * Returns the URL for an admin tool of specific application.
 * @param {string} application Full application name (f.ex, 'com.enonic.app')
 * @param {string} tool Name of the tool inside an app (f.ex, 'main')
 *
 * @returns {string} URL.
 */
export function getToolUrl(application: string, tool: string): string {
    return portal.url({
        path: '/admin/' + application + '/' + tool,
    });
}

/**
 * Returns the URL for the Home admin tool.
 * @param {object} [params] Parameter object
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 *
 * @returns {string} URL.
 */
export function getHomeToolUrl(params?: GetHomeToolUrlParams): string {
    return portal.url({
        path: '/admin',
        type: params?.type,
    });
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

    return portal.apiUrl({
        application: 'admin',
        api: 'widget',
        type: params.type,
        path: [params.application, params.widget],
        params: params.params || {},
    });
}
