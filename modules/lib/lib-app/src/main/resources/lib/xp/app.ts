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

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export interface CreateVirtualApplicationParams {
    key: string;
}

export interface Application {
    key: string;
    displayName: string | null;
    vendorName: string | null;
    vendorUrl: string | null;
    url: string | null;
    version: string | null;
    systemVersion: string | null;
    minSystemVersion: string | null;
    maxSystemVersion: string | null;
    modifiedTime: string | null;
    started: boolean | null;
    system: boolean | null;
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
 * @returns {object} created application.
 */
export function createVirtualApplication(params: CreateVirtualApplicationParams): Application {
    checkRequired(params, 'key');

    const bean: CreateVirtualApplicationHandler = __.newBean<CreateVirtualApplicationHandler>('com.enonic.xp.lib.app.CreateVirtualApplicationHandler');
    bean.setKey(params.key);
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
    checkRequired(params, 'key');

    const bean: DeleteVirtualApplicationHandler = __.newBean<DeleteVirtualApplicationHandler>('com.enonic.xp.lib.app.DeleteVirtualApplicationHandler');
    bean.setKey(params.key);
    return __.toNativeObject(bean.execute());
}

export interface GetApplicationParams {
    key: string;
}

interface GetApplicationHandler {
    setKey(value: string): void;

    execute(): Application;
}

/**
 * Fetches application by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {Application} fetched application.
 */
export function get(params: GetApplicationParams): Application {
    checkRequired(params, 'key');

    const bean: GetApplicationHandler = __.newBean<GetApplicationHandler>('com.enonic.xp.lib.app.GetApplicationHandler');
    bean.setKey(params.key);
    return __.toNativeObject(bean.execute());
}

interface ListApplicationsHandler {
    execute(): Application[];
}

/**
 * Fetches both static and virtual applications.
 *
 * @returns {object[]} applications list.
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
    description: string | null;
    icon?: Icon;
}

interface GetApplicationDescriptorHandler {
    setKey(value: string): void;

    execute(): ApplicationDescriptor;
}

/**
 * Fetches application descriptor by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {object} fetched application descriptor.
 */
export function getDescriptor(params: GetApplicationDescriptorParams): ApplicationDescriptor {
    const bean: GetApplicationDescriptorHandler = __.newBean<GetApplicationDescriptorHandler>('com.enonic.xp.lib.app.GetApplicationDescriptorHandler');
    bean.setKey(params.key);
    return __.toNativeObject(bean.execute());
}

export interface HasVirtualApplicationParams {
    key: string;
}

interface HasVirtualApplicationHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Checks if there is a virtual app with the app key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} result.
 */
export function hasVirtual(params: HasVirtualApplicationParams): boolean {
    checkRequired(params, 'key');

    const bean: HasVirtualApplicationHandler = __.newBean<HasVirtualApplicationHandler>('com.enonic.xp.lib.app.HasVirtualApplicationHandler');
    bean.setKey(params.key);
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
 * @returns {string} application mode.
 */
export function getApplicationMode(params: GetApplicationModeParams): string | null {
    checkRequired(params, 'key');

    const bean: GetApplicationModeHandler = __.newBean<GetApplicationModeHandler>('com.enonic.xp.lib.app.GetApplicationModeHandler');
    bean.setKey(params.key);
    return __.toNativeObject(bean.execute());
}
