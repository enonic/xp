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

export interface CreateVirtualApplicationParams {
    key: string;
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

interface CreateVirtualApplicationHandler {
    setKey(value: string): void;

    execute(): Application;
}

/**
 * Creates virtual application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {Application} created application.
 */
export function createVirtualApplication(params: CreateVirtualApplicationParams): Application {
    const key = checkRequired(params, 'key');

    const bean: CreateVirtualApplicationHandler = __.newBean<CreateVirtualApplicationHandler>('com.enonic.xp.lib.app.CreateVirtualApplicationHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface DeleteVirtualApplicationParams {
    key: string;
}

interface DeleteVirtualApplicationHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Deletes virtual application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} deletion result.
 */
export function deleteVirtualApplication(params: DeleteVirtualApplicationParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteVirtualApplicationHandler = __.newBean<DeleteVirtualApplicationHandler>('com.enonic.xp.lib.app.DeleteVirtualApplicationHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface GetApplicationParams {
    key: string;
}

interface GetApplicationHandler {
    setKey(value: string): void;

    execute(): Application | null;
}

/**
 * Fetches application by key.
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
 * Fetches both static and virtual applications.
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
    modifiedTime: string;
}

export interface ApplicationDescriptor {
    key: string;
    description: string;
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

export interface GetApplicationModeParams {
    key: string;
}

interface GetApplicationModeHandler {
    setKey(value: string): void;

    execute(): string | null;
}

/**
 * Fetches a mode of the app with the app key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {string | null} application mode, or null if the application is not installed.
 */
export function getApplicationMode(params: GetApplicationModeParams): string | null {
    const key = checkRequired(params, 'key');

    const bean: GetApplicationModeHandler = __.newBean<GetApplicationModeHandler>('com.enonic.xp.lib.app.GetApplicationModeHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}
