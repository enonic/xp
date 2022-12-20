/**
 * Dynamic schema related functions.
 *
 * @example
 * var schema = require('/lib/xp/schema');
 *
 * @module schema
 */

declare global {
    interface XpLibraries {
        '/lib/xp/schema': typeof import('./schema');
    }
}

import type {ByteSource, FormItem, UserKey} from '@enonic-types/core';

export type {
    ByteSource,
    GroupKey,
    PrincipalKey,
    RoleKey,
    UserKey,
    FormItemSet,
    FormItemLayout,
    FormItemInput,
    FormItemOptionSet,
    FormItemInlineMixin,
    FormItem,
} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export interface Icon {
    data: ByteSource;
    mimeType: string;
    modifiedTime: string;
}

export interface CreateDynamicContentSchemaParams {
    name: string;
    type: string;
    resource?: string | null;
}

interface CreateDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: string): void;

    setResource(value: string): void;

    execute(): ContentTypeSchema | MixinSchema | XDataSchema;
}

export type ContentSchemaType = 'CONTENT_TYPE' | 'MIXIN' | 'XDATA';

export interface Schema {
    name: string;
    displayName: string;
    displayNameI18nKey: string;
    description: string;
    descriptionI18nKey: string;
    createdTime: string;
    creator: UserKey;
    modifiedTime: string;
    modifier: UserKey;
    resource: string;
    type: ContentSchemaType;
    icon?: Icon;
}

export interface ContentTypeSchema
    extends Schema {
    form: FormItem[];
    config: {
        [configName: string]: {
            [attributeKey: string]: string;
            value: string;
        }[]
    };
    xDataNames?: string[];
}

export interface MixinSchema
    extends Schema {
    form: FormItem[];
}

export interface XDataSchema
    extends Schema {
    form: FormItem[];
}

/**
 * Creates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Schema resource name.
 * @param {string} params.type Schema type.
 * @param {string} [params.resource] Schema resource value.
 *
 * @returns {ContentTypeSchema | MixinSchema | XDataSchema} created resource.
 */
export function createSchema(params: CreateDynamicContentSchemaParams): ContentTypeSchema | MixinSchema | XDataSchema {
    checkRequired(params, 'name');
    checkRequired(params, 'type');
    checkRequired(params, 'resource');

    const bean = __.newBean<CreateDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(params.name);
    bean.setType(params.type);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export type ComponentDescriptorType = 'PAGE' | 'LAYOUT' | 'PART';

export interface CreateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

interface CreateDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: string): void;

    setResource(value?: string | null): void;

    execute(): LayoutDescriptor | PageDescriptor | PartDescriptor;
}

export interface ComponentDescriptor {
    key: string;
    displayName: string;
    displayNameI18nKey: string;
    description: string;
    descriptionI18nKey: string;
    componentPath: string;
    modifiedTime: string;
    resource: string;
    type: ComponentDescriptorType;
    form: FormItem[];
    config: {
        [configName: string]: {
            [attributeKey: string]: string;
            value: string;
        }[]
    };
}

export interface LayoutDescriptor
    extends ComponentDescriptor {
    regions?: string[] | null;
}

export interface PageDescriptor
    extends ComponentDescriptor {
    regions?: string[] | null;
}

export interface PartDescriptor
    extends ComponentDescriptor {
    icon?: Icon | null;
}

/**
 * Creates dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} [params.resource] Component resource value.
 *
 * @returns {string} created resource.
 */
export function createComponent(params: CreateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    checkRequired(params, 'key');
    checkRequired(params, 'type');
    checkRequired(params, 'resource');

    const bean = __.newBean<CreateDynamicComponentHandler>('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');

    bean.setKey(params.key);
    bean.setType(params.type);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export interface CreateDynamicStylesParams {
    application: string;
    resource: string;
}

interface CreateDynamicStylesHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): StyleDescriptor;
}

export interface StyleDescriptor {
    application: string;
    cssPath: string;
    modifiedTime: string;
    resource: string;
    elements?: {
        element: string;
        displayName: string;
        name: string;
    }[];
}

/**
 * Creates dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} [params.resource] Styles resource value.
 *
 * @returns {StyleDescriptor} created resource.
 */
export function createStyles(params: CreateDynamicStylesParams): StyleDescriptor {
    checkRequired(params, 'application');
    checkRequired(params, 'resource');

    const bean = __.newBean<CreateDynamicStylesHandler>('com.enonic.xp.lib.schema.CreateDynamicStylesHandler');
    bean.setApplication(params.application);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
}

interface GetDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): ContentTypeSchema | MixinSchema | XDataSchema;
}

/**
 * Fetches dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentTypeSchema | MixinSchema | XDataSchema} fetched resource.
 */
export function getSchema(params: GetDynamicContentSchemaParams): ContentTypeSchema | MixinSchema | XDataSchema {
    checkRequired(params, 'name');
    checkRequired(params, 'type');

    const bean = __.newBean<GetDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(params.name);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
}

interface GetDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): ContentTypeSchema | MixinSchema | XDataSchema;
}

/**
 * Fetches dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {string} fetched resource.
 */
export function getComponent(params: GetDynamicComponentParams): unknown {
    checkRequired(params, 'key');
    checkRequired(params, 'type');

    const bean = __.newBean<GetDynamicComponentHandler>('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(params.key);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}

export interface SiteDescriptor {
    application: string;
    resource: string;
    modifiedTime: string;
    form: FormItem[];
    xDataMappings?: {
        name: string;
        optional: boolean;
        allowContentTypes: string;
    }[];
}

export interface GetDynamicSiteParams {
    application: string;
}

interface GetDynamicSiteHandler {
    setApplication(value: string): void;

    execute(): SiteDescriptor | null;
}

/**
 * Fetches dynamic site schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {SiteDescriptor} fetched resource.
 */
export function getSite(params: GetDynamicSiteParams): SiteDescriptor | null {
    checkRequired(params, 'application');

    const bean = __.newBean<GetDynamicSiteHandler>('com.enonic.xp.lib.schema.GetDynamicSiteHandler');
    bean.setApplication(params.application);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicStylesParams {
    application: string;
}

interface GetDynamicStylesHandler {
    setApplication(value: string): void;

    execute(): StyleDescriptor | null;
}

/**
 * Fetches dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application application key.
 *
 * @returns {StyleDescriptor} fetched resource.
 */
export function getStyles(params: GetDynamicStylesParams): StyleDescriptor | null {
    checkRequired(params, 'application');

    const bean = __.newBean<GetDynamicStylesHandler>('com.enonic.xp.lib.schema.GetDynamicStylesHandler');
    bean.setApplication(params.application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
}

interface DeleteDynamicContentSchemaHandler {
    setName(value: string);

    setType(value: ContentSchemaType);

    execute(): boolean;
}

/**
 * Removes dynamic schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteSchema(params: DeleteDynamicContentSchemaParams): boolean {
    checkRequired(params, 'name');
    checkRequired(params, 'type');

    const bean = __.newBean<DeleteDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(params.name);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
}

interface DeleteDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): boolean;
}

/**
 * Removes dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteComponent(params: DeleteDynamicComponentParams): boolean {
    checkRequired(params, 'key');
    checkRequired(params, 'type');

    const bean = __.newBean<DeleteDynamicComponentHandler>('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(params.key);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicStylesParams {
    application: string;
}

interface DeleteDynamicStylesHandler {
    setApplication(value: string): void;

    execute(): boolean;
}

/**
 * Removes dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteStyles(params: DeleteDynamicStylesParams): boolean {
    checkRequired(params, 'application');

    const bean = __.newBean<DeleteDynamicStylesHandler>('com.enonic.xp.lib.schema.DeleteDynamicStylesHandler');
    bean.setApplication(params.application);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

interface UpdateDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    setResource(value: string): void;

    execute(): ContentTypeSchema | MixinSchema | XDataSchema;
}

/**
 * Updates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 * @param {string} [params.resource] Schema resource value.
 *
 * @returns {ContentTypeSchema | MixinSchema | XDataSchema} created resource.
 */
export function updateSchema(params: UpdateDynamicContentSchemaParams): ContentTypeSchema | MixinSchema | XDataSchema {
    checkRequired(params, 'name');
    checkRequired(params, 'type');
    checkRequired(params, 'resource');

    const bean = __.newBean<UpdateDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(params.name);
    bean.setType(params.type);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

interface UpdateDynamicComponentHandler {
    setKey(key: string): void;

    setType(key: ComponentDescriptorType): void;

    setResource(key: string): void;

    execute(): LayoutDescriptor | PageDescriptor | PartDescriptor;
}

/**
 * Updates dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} [params.resource] Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} created resource.
 */
export function updateComponent(params: UpdateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    checkRequired(params, 'key');
    checkRequired(params, 'type');
    checkRequired(params, 'resource');

    const bean = __.newBean<UpdateDynamicComponentHandler>('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(params.key);
    bean.setType(params.type);
    bean.setResource(params.resource);

    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicSiteParams {
    application: string;
    resource: string;
}

interface UpdateDynamicSiteHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): SiteDescriptor;
}

/**
 * Updates dynamic site schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} [params.resource] Site schema resource value.
 *
 * @returns {string} created resource.
 */
export function updateSite(params: UpdateDynamicSiteParams): SiteDescriptor {
    checkRequired(params, 'application');
    checkRequired(params, 'resource');

    const bean = __.newBean<UpdateDynamicSiteHandler>('com.enonic.xp.lib.schema.UpdateDynamicSiteHandler');
    bean.setApplication(params.application);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicStylesParams {
    application: string;
    resource: string;
}

interface UpdateDynamicStylesHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): StyleDescriptor;
}

/**
 * Updates dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} [params.resource] Styles schema resource value.
 *
 * @returns {string} created resource.
 */
export function updateStyles(params: UpdateDynamicStylesParams): StyleDescriptor {
    checkRequired(params, 'application');
    checkRequired(params, 'resource');

    const bean = __.newBean<UpdateDynamicStylesHandler>('com.enonic.xp.lib.schema.UpdateDynamicStylesHandler');
    bean.setApplication(params.application);
    bean.setResource(params.resource);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicComponentsParams {
    application: string;
    type: ComponentDescriptorType;
}

interface ListDynamicComponentsHandler {
    setApplication(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[];
}

/**
 * Fetches dynamic component resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.type Component type.
 *
 * @returns {PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[]} fetched resources.
 */
export function listComponents(params: ListDynamicComponentsParams): PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[] {
    checkRequired(params, 'application');
    checkRequired(params, 'type');

    const bean = __.newBean<ListDynamicComponentsHandler>('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(params.application);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicSchemasParams {
    application: string;
    type: ContentSchemaType;
}

interface ListDynamicSchemasHandler {
    setApplication(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): ContentSchemaType[] | MixinSchema[] | XDataSchema[];
}

/**
 * Fetches dynamic content schemas resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentSchemaType[] | MixinSchema[] | XDataSchema[]} fetched resources.
 */
export function listSchemas(params: ListDynamicSchemasParams): ContentSchemaType[] | MixinSchema[] | XDataSchema[] {
    checkRequired(params, 'application');
    checkRequired(params, 'type');

    const bean = __.newBean<ListDynamicSchemasHandler>('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(params.application);
    bean.setType(params.type);
    return __.toNativeObject(bean.execute());
}
