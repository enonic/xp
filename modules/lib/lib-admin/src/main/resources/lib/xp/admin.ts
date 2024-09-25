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

const i18n = require('/lib/xp/i18n');
const portal = require('/lib/xp/portal');

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

const helper = __.newBean<AdminLibHelper>('com.enonic.xp.lib.admin.AdminLibHelper');

interface AdminLibHelper {
    getHomeAppName(): string;

    generateAdminToolUri(application: string, adminTool: string): string;

    getAssetsUri(): string;

    getBaseUri(): string;

    getHomeToolUri(): string;

    getInstallation(): string;

    getLauncherToolUrl(): string;

    getLocale(): string;

    getLocales(): string[];

    getPhrases(): string;

    getVersion(): string;
}

/**
 * Returns the admin base uri.
 *
 * @returns {string} Admin base uri.
 */
export function getBaseUri(): string {
    return helper.getBaseUri();
}

/**
 * Returns the admin assets uri.
 *
 * @returns {string} Assets uri.
 */
export function getAssetsUri(): string {
    return helper.getAssetsUri();
}

/**
 * Returns the preferred locale based on the current HTTP request, or the server default locale if none is specified.
 *
 * @returns {string} Current locale.
 */
export function getLocale(): string {
    return helper.getLocale();
}

/**
 * Returns the list of preferred locales based on the current HTTP request, or the server default locale if none is specified.
 *
 * @returns {string[]} Current locales in order of preference.
 */
export function getLocales(): string[] {
    return __.toNativeObject(helper.getLocales());
}

/**
 * Returns all i18n phrases.
 *
 * @returns {object} JSON object with phrases.
 */
export function getPhrases(): string {
    return JSON.stringify(i18n.getPhrases(getLocales(), ['i18n/common', 'i18n/phrases']));
}

/**
 * Returns the URL for launcher panel.
 *
 * @returns {string} URL.
 */
export function getLauncherUrl(): string {
    return helper.getLauncherToolUrl();
}

/**
 * Returns the URL for launcher javascript.
 *
 * @returns {string} Path.
 */
export function getLauncherPath(): string {
    return portal.assetUrl({
        application: helper.getHomeAppName(),
        path: '/js/launcher/bundle.js',
    });
}

/**
 * Returns the URL for an admin tool of specific application.
 * @param {string} application Full application name (f.ex, 'com.enonic.app')
 * @param {string} tool Name of the tool inside an app (f.ex, 'main')
 *
 * @returns {string} URL.
 */
export function getToolUrl(application: string, tool: string): string {
    if (application) {
        return helper.generateAdminToolUri(application, tool);
    }

    return helper.getHomeToolUri();
}

/**
 * Returns the URL for the Home admin tool.
 * @param {object} [params] Parameter object
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 *
 * @returns {string} URL.
 */
export function getHomeToolUrl(params: GetHomeToolUrlParams): string {
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
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} URL.
 */
export function widgetUrl(params: WidgetUrlParams): string {
    checkRequired(params, 'application');
    checkRequired(params, 'widget');

    return portal.apiUrl({
        application: 'admin',
        api: 'widget',
        type: params.type || 'server',
        path: [params.application, params.widget],
        params: params.params || {},
    });
}
