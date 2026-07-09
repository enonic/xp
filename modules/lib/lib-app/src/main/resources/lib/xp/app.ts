/**
 * Application related functions.
 *
 * @example
 * var schema = require('/lib/xp/app');
 *
 * @module app
 */

declare global {
    interface XpLibraries {
        '/lib/xp/app': typeof import('./app');
    }
}

import type {ByteSource} from '@enonic-types/core';

export type {ByteSource} from '@enonic-types/core';

function checkRequired<T extends object, K extends keyof T>(
    obj: T,
    name: K,
): NonNullable<T[K]> {
    if (obj == null || obj[name] == null) {
        throw new Error(`Parameter '${String(name)}' is required`);
    }
    return obj[name];
}

export interface Application {
    key: string;
    version: string | null;
    systemVersion: string | null;
    minSystemVersion: string | null;
    maxSystemVersion: string | null;
    modifiedTime: string | null;
    started: boolean;
    system: boolean;
}

export interface GetApplicationParams {
    key: string;
}

interface GetApplicationHandler {
    setKey(value: string): void;

    execute(): Application | null;
}

/**
 * Fetches an installed application by key. Namespaces (virtual applications) are not taken into account.
 *
 * @deprecated Returns installed applications only; namespaces (virtual applications) are not included.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {Application | null} fetched application, or null if not found.
 */
export function get(params: GetApplicationParams): Application | null {
    const key = checkRequired(params, 'key');

    const bean: GetApplicationHandler = __.newBean<GetApplicationHandler>('com.enonic.xp.lib.app.GetApplicationHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

interface ListApplicationsHandler {
    execute(): Application[];
}

/**
 * Fetches installed applications. Namespaces (virtual applications) are not taken into account.
 *
 * @deprecated Returns installed applications only; namespaces (virtual applications) are not included.
 *
 * @returns {Application[]} applications list.
 */
export function list(): Application[] {
    const bean: ListApplicationsHandler = __.newBean<ListApplicationsHandler>('com.enonic.xp.lib.app.ListApplicationsHandler');
    return __.toNativeObject(bean.execute());
}

export interface GetApplicationDescriptorParams {
    key: string;
}

export interface Icon {
    data: ByteSource;
    mimeType: string;
    /**
     * @deprecated Not a dependable measure of when the icon last changed, and unsuitable for cache
     * invalidation. Icons are loaded from application resources, so this derives from a jar entry timestamp
     * that build tools normalize to a constant for reproducibility, from the install time of the bundle
     * providing the icon, or from the time the icon happened to be read.
     */
    modifiedTime: string;
}

export interface ApplicationDescriptor {
    key: string;
    description: string;
    descriptionI18nKey: string | null;
    title: string | null;
    titleI18nKey: string | null;
    vendorName: string | null;
    vendorUrl: string | null;
    url: string | null;
    icon?: Icon;
}

interface GetApplicationDescriptorHandler {
    setKey(value: string): void;

    execute(): ApplicationDescriptor | null;
}

/**
 * Fetches application descriptor by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {ApplicationDescriptor | null} fetched application descriptor, or null if not found.
 */
export function getDescriptor(params: GetApplicationDescriptorParams): ApplicationDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetApplicationDescriptorHandler = __.newBean<GetApplicationDescriptorHandler>('com.enonic.xp.lib.app.GetApplicationDescriptorHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}
