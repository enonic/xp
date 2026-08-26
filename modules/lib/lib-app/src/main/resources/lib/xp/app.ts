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

import type {ByteSource, ConfigValue, ScriptValue} from '@enonic-types/core';

export type {ByteSource, ConfigObject, ConfigValue, ScriptValue} from '@enonic-types/core';

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
    schema: boolean;
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
 * Fetches all applications.
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
    config: Record<string, ConfigValue>;
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

    const bean: GetApplicationDescriptorHandler = __.newBean<GetApplicationDescriptorHandler>(
        'com.enonic.xp.lib.app.GetApplicationDescriptorHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface IconInput {
    data: ByteSource;
    mimeType: string;
}

/**
 * The application descriptor as handed to the editor of {@link createOrUpdate}: the fields of {@link ApplicationDescriptor},
 * with the icon replaceable by `{data, mimeType}` or removable by `null`.
 */
export interface EditableApplicationDescriptor
    extends Omit<ApplicationDescriptor, 'icon'> {
    icon?: Icon | IconInput | null;
}

export type ApplicationDescriptorEditor = (descriptor: EditableApplicationDescriptor) => EditableApplicationDescriptor;

export interface CreateOrUpdateApplicationDescriptorParams {
    key: string;
    editor: ApplicationDescriptorEditor;
}

interface CreateOrUpdateApplicationDescriptorHandler {
    setKey(value: string): void;

    setEditor(value: ScriptValue | null): void;

    execute(): ApplicationDescriptor;
}

/**
 * Creates or updates the application descriptor (`enonic.yaml`) persisted in system-repo, and its icon.
 *
 * Pass an `editor` function that receives the current descriptor, as {@link getDescriptor} returns it, mutates the fields
 * it wants to change and returns it. Fields left untouched keep their value, fields set to `null` are cleared.
 * The icon is kept when left as received, removed when set to `null`, and replaced when set to `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * An application without a node in system-repo gets one, so a descriptor can be created before any bundle is installed:
 * the editor then receives a descriptor holding only the key. Installing a bundle of the application later persists the
 * bundle's descriptor over it.
 *
 * Requires the `system.admin` or `system.schema.admin` role.
 *
 * @example-ref examples/app/createOrUpdate.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {Function} params.editor Function that receives the editable descriptor, mutates fields, and returns it.
 *
 * @returns {ApplicationDescriptor} The descriptor as read after the change.
 */
export function createOrUpdate(params: CreateOrUpdateApplicationDescriptorParams): ApplicationDescriptor {
    const key = checkRequired(params, 'key');
    const editor = checkRequired(params, 'editor');

    const bean: CreateOrUpdateApplicationDescriptorHandler = __.newBean<CreateOrUpdateApplicationDescriptorHandler>(
        'com.enonic.xp.lib.app.CreateOrUpdateApplicationDescriptorHandler');
    bean.setKey(key);
    bean.setEditor(__.toScriptValue(editor));
    return __.toNativeObject(bean.execute());
}
