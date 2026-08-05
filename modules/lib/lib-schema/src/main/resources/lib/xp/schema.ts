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

import type {ByteSource, ConfigObject, ConfigValue, FormItem, UserKey} from '@enonic-types/core';

export type {
    ByteSource,
    FormItem,
    FormItemFormFragment,
    FormItemInput,
    FormItemLayout,
    FormItemOptionSet,
    FormItemSet,
    GroupKey,
    InputType,
    PrincipalKey,
    RoleKey,
    UserKey,
    ValueType,
    ConfigValue,
} from '@enonic-types/core';

function checkRequired<T extends object, K extends keyof T>(
    obj: T,
    name: K,
): NonNullable<T[K]> {
    if (obj == null || obj[name] == null) {
        throw new Error(`Parameter '${String(name)}' is required`);
    }
    return obj[name];
}

export interface Icon {
    data: ByteSource;
    mimeType: string;
    modifiedTime: string;
}

export type ContentSchemaType = 'CONTENT_TYPE' | 'FORM_FRAGMENT' | 'MIXIN';

export interface CreateDynamicContentSchemaParams {
    name: string;
    resource: string;
}

interface CreateDynamicContentSchemaHandler<T extends Schema> {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    setResource(value: string): void;

    execute(): T;
}

export interface Schema {
    name: string;
    title: string;
    titleI18nKey: string;
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
    superType?: string | null;
    abstract?: boolean;
    final?: boolean;
    allowChildContent?: boolean;
    allowChildContentType?: string[];
    displayNamePlaceholder?: string | null;
    displayNamePlaceholderI18nKey?: string | null;
    displayNameExpression?: string | null;
    displayNameListExpression?: string | null;
    form: FormItem[];
    config: Record<string, ConfigValue>;
    mixinNames?: string[];
}

export interface FormFragmentSchema
    extends Schema {
    form: FormItem[];
}

export interface MixinSchema
    extends Schema {
    form: FormItem[];
    config: Record<string, ConfigValue>;
}

/**
 * Creates dynamic content type resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type resource name.
 * @param {string} params.resource Content type resource value.
 *
 * @returns {ContentTypeSchema} created resource.
 */
export function createContentType(params: CreateDynamicContentSchemaParams): ContentTypeSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicContentSchemaHandler<ContentTypeSchema> = __.newBean<CreateDynamicContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('CONTENT_TYPE');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates dynamic form fragment resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment resource name.
 * @param {string} params.resource Form fragment resource value.
 *
 * @returns {FormFragmentSchema} created resource.
 */
export function createFormFragment(params: CreateDynamicContentSchemaParams): FormFragmentSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicContentSchemaHandler<FormFragmentSchema> = __.newBean<CreateDynamicContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('FORM_FRAGMENT');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates dynamic mixin resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin resource name.
 * @param {string} params.resource Mixin resource value.
 *
 * @returns {MixinSchema} created resource.
 */
export function createMixin(params: CreateDynamicContentSchemaParams): MixinSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicContentSchemaHandler<MixinSchema> = __.newBean<CreateDynamicContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export type ComponentDescriptorType = 'PAGE' | 'LAYOUT' | 'PART';

export interface CreateDynamicComponentParams {
    key: string;
    resource: string;
}

interface CreateDynamicComponentHandler<T extends ComponentDescriptor> {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    setResource(value: string): void;

    execute(): T;
}

export interface ComponentDescriptor {
    key: string;
    title: string;
    titleI18nKey: string;
    description: string;
    descriptionI18nKey: string;
    componentPath: string;
    modifiedTime: string;
    resource: string;
    type: ComponentDescriptorType;
    form: FormItem[];
    config: Record<string, ConfigValue>;
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
 * Creates dynamic part resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part resource descriptor key.
 * @param {string} params.resource Part resource value.
 *
 * @returns {PartDescriptor} created resource.
 */
export function createPart(params: CreateDynamicComponentParams): PartDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicComponentHandler<PartDescriptor> = __.newBean<CreateDynamicComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');

    bean.setKey(key);
    bean.setType('PART');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates dynamic layout resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Layout resource descriptor key.
 * @param {string} params.resource Layout resource value.
 *
 * @returns {LayoutDescriptor} created resource.
 */
export function createLayout(params: CreateDynamicComponentParams): LayoutDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicComponentHandler<LayoutDescriptor> = __.newBean<CreateDynamicComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');

    bean.setKey(key);
    bean.setType('LAYOUT');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates dynamic page resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Page resource descriptor key.
 * @param {string} params.resource Page resource value.
 *
 * @returns {PageDescriptor} created resource.
 */
export function createPage(params: CreateDynamicComponentParams): PageDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicComponentHandler<PageDescriptor> = __.newBean<CreateDynamicComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');

    bean.setKey(key);
    bean.setType('PAGE');
    bean.setResource(resource);
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

export type EditorConfig = ConfigObject & {
    css?: string;
};

export interface StyleDescriptor {
    application: string;
    modifiedTime: string;
    resource: string;
    elements: {
        label: string | null;
        name: string;
        type: string;
        aspectRatio?: string | null;
        filter?: string | null;
        editor?: EditorConfig;
    }[];
}

/**
 * Creates dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Styles resource value.
 *
 * @returns {StyleDescriptor} created resource.
 */
export function createStyles(params: CreateDynamicStylesParams): StyleDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicStylesHandler = __.newBean<CreateDynamicStylesHandler>('com.enonic.xp.lib.schema.CreateDynamicStylesHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicContentSchemaParams {
    name: string;
}

interface GetDynamicContentSchemaHandler<T extends Schema> {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): T | null;
}

/**
 * Fetches dynamic content type resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type resource name.
 *
 * @returns {ContentTypeSchema | null} fetched resource, or `null` if not found.
 */
export function getContentType(params: GetDynamicContentSchemaParams): ContentTypeSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetDynamicContentSchemaHandler<ContentTypeSchema> = __.newBean<GetDynamicContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('CONTENT_TYPE');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic form fragment resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment resource name.
 *
 * @returns {FormFragmentSchema | null} fetched resource, or `null` if not found.
 */
export function getFormFragment(params: GetDynamicContentSchemaParams): FormFragmentSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetDynamicContentSchemaHandler<FormFragmentSchema> = __.newBean<GetDynamicContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('FORM_FRAGMENT');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic mixin resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin resource name.
 *
 * @returns {MixinSchema | null} fetched resource, or `null` if not found.
 */
export function getMixin(params: GetDynamicContentSchemaParams): MixinSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetDynamicContentSchemaHandler<MixinSchema> = __.newBean<GetDynamicContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicComponentParams {
    key: string;
}

interface GetDynamicComponentHandler<T extends ComponentDescriptor> {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): T | null;
}

/**
 * Fetches dynamic part resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part resource descriptor key.
 *
 * @returns {PartDescriptor | null} fetched resource, or `null` if not found.
 */
export function getPart(params: GetDynamicComponentParams): PartDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicComponentHandler<PartDescriptor> = __.newBean<GetDynamicComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PART');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic layout resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Layout resource descriptor key.
 *
 * @returns {LayoutDescriptor | null} fetched resource, or `null` if not found.
 */
export function getLayout(params: GetDynamicComponentParams): LayoutDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicComponentHandler<LayoutDescriptor> = __.newBean<GetDynamicComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('LAYOUT');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic page resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Page resource descriptor key.
 *
 * @returns {PageDescriptor | null} fetched resource, or `null` if not found.
 */
export function getPage(params: GetDynamicComponentParams): PageDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicComponentHandler<PageDescriptor> = __.newBean<GetDynamicComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
    return __.toNativeObject(bean.execute());
}

export interface CmsDescriptor {
    application: string;
    resource: string;
    modifiedTime: string;
    form: FormItem[];
    mixinMappings?: {
        name: string;
        optional: boolean;
        allowContentTypes: string;
    }[];
}

export interface GetDynamicCmsParams {
    application: string;
}

interface GetDynamicCmsHandler {
    setApplication(value: string): void;

    execute(): CmsDescriptor | null;
}

/**
 * Fetches dynamic CMS schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {CmsDescriptor | null} fetched resource, or `null` if not found.
 */
export function getCms(params: GetDynamicCmsParams): CmsDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetDynamicCmsHandler = __.newBean<GetDynamicCmsHandler>('com.enonic.xp.lib.schema.GetDynamicCmsHandler');
    bean.setApplication(application);
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
 * @param {string} params.application Application key.
 *
 * @returns {StyleDescriptor | null} fetched resource, or `null` if not found.
 */
export function getStyles(params: GetDynamicStylesParams): StyleDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetDynamicStylesHandler = __.newBean<GetDynamicStylesHandler>('com.enonic.xp.lib.schema.GetDynamicStylesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicContentSchemaParams {
    name: string;
}

interface DeleteDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): boolean;
}

/**
 * Removes dynamic content type resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteContentType(params: DeleteDynamicContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicContentSchemaHandler = __.newBean<DeleteDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('CONTENT_TYPE');
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic form fragment resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteFormFragment(params: DeleteDynamicContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicContentSchemaHandler = __.newBean<DeleteDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('FORM_FRAGMENT');
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic mixin resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMixin(params: DeleteDynamicContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicContentSchemaHandler = __.newBean<DeleteDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicComponentParams {
    key: string;
}

interface DeleteDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): boolean;
}

/**
 * Removes dynamic part resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deletePart(params: DeleteDynamicComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicComponentHandler = __.newBean<DeleteDynamicComponentHandler>('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PART');
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic layout resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Layout resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteLayout(params: DeleteDynamicComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicComponentHandler = __.newBean<DeleteDynamicComponentHandler>('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('LAYOUT');
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic page resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Page resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deletePage(params: DeleteDynamicComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicComponentHandler = __.newBean<DeleteDynamicComponentHandler>('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
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
    const application = checkRequired(params, 'application');

    const bean: DeleteDynamicStylesHandler = __.newBean<DeleteDynamicStylesHandler>('com.enonic.xp.lib.schema.DeleteDynamicStylesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicContentSchemaParams {
    name: string;
    resource: string;
}

interface UpdateDynamicContentSchemaHandler<T extends Schema> {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    setResource(value: string): void;

    execute(): T;
}

/**
 * Updates dynamic content type resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type resource name.
 * @param {string} params.resource Content type resource value.
 *
 * @returns {ContentTypeSchema} updated resource.
 */
export function updateContentType(params: UpdateDynamicContentSchemaParams): ContentTypeSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicContentSchemaHandler<ContentTypeSchema> = __.newBean<UpdateDynamicContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('CONTENT_TYPE');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Updates dynamic form fragment resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment resource name.
 * @param {string} params.resource Form fragment resource value.
 *
 * @returns {FormFragmentSchema} updated resource.
 */
export function updateFormFragment(params: UpdateDynamicContentSchemaParams): FormFragmentSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicContentSchemaHandler<FormFragmentSchema> = __.newBean<UpdateDynamicContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('FORM_FRAGMENT');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Updates dynamic mixin resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin resource name.
 * @param {string} params.resource Mixin resource value.
 *
 * @returns {MixinSchema} updated resource.
 */
export function updateMixin(params: UpdateDynamicContentSchemaParams): MixinSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicContentSchemaHandler<MixinSchema> = __.newBean<UpdateDynamicContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicComponentParams {
    key: string;
    resource: string;
}

interface UpdateDynamicComponentHandler<T extends ComponentDescriptor> {
    setKey(key: string): void;

    setType(key: ComponentDescriptorType): void;

    setResource(key: string): void;

    execute(): T;
}

/**
 * Updates dynamic part resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part resource descriptor key.
 * @param {string} params.resource Part resource value.
 *
 * @returns {PartDescriptor} updated resource.
 */
export function updatePart(params: UpdateDynamicComponentParams): PartDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicComponentHandler<PartDescriptor> = __.newBean<UpdateDynamicComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PART');
    bean.setResource(resource);

    return __.toNativeObject(bean.execute());
}

/**
 * Updates dynamic layout resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Layout resource descriptor key.
 * @param {string} params.resource Layout resource value.
 *
 * @returns {LayoutDescriptor} updated resource.
 */
export function updateLayout(params: UpdateDynamicComponentParams): LayoutDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicComponentHandler<LayoutDescriptor> = __.newBean<UpdateDynamicComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('LAYOUT');
    bean.setResource(resource);

    return __.toNativeObject(bean.execute());
}

/**
 * Updates dynamic page resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Page resource descriptor key.
 * @param {string} params.resource Page resource value.
 *
 * @returns {PageDescriptor} updated resource.
 */
export function updatePage(params: UpdateDynamicComponentParams): PageDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicComponentHandler<PageDescriptor> = __.newBean<UpdateDynamicComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
    bean.setResource(resource);

    return __.toNativeObject(bean.execute());
}

export interface CreateDynamicCmsParams {
    application: string;
    resource: string;
}

interface CreateDynamicCmsHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): CmsDescriptor;
}

/**
 * Creates dynamic CMS schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource CMS schema resource value.
 *
 * @returns {CmsDescriptor} created resource.
 */
export function createCms(params: CreateDynamicCmsParams): CmsDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicCmsHandler = __.newBean<CreateDynamicCmsHandler>('com.enonic.xp.lib.schema.CreateDynamicCmsHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicCmsParams {
    application: string;
    resource: string;
}

interface UpdateDynamicCmsHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): CmsDescriptor;
}

/**
 * Updates dynamic CMS schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource CMS schema resource value.
 *
 * @returns {CmsDescriptor} updated resource.
 */
export function updateCms(params: UpdateDynamicCmsParams): CmsDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicCmsHandler = __.newBean<UpdateDynamicCmsHandler>('com.enonic.xp.lib.schema.UpdateDynamicCmsHandler');
    bean.setApplication(application);
    bean.setResource(resource);
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
 * @param {string} params.resource Styles schema resource value.
 *
 * @returns {StyleDescriptor} updated resource.
 */
export function updateStyles(params: UpdateDynamicStylesParams): StyleDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicStylesHandler = __.newBean<UpdateDynamicStylesHandler>('com.enonic.xp.lib.schema.UpdateDynamicStylesHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicCmsParams {
    application: string;
}

interface DeleteDynamicCmsHandler {
    setApplication(value: string): void;

    execute(): boolean;
}

/**
 * Removes dynamic CMS descriptor resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {boolean} `true` if the resource was removed.
 */
export function deleteCms(params: DeleteDynamicCmsParams): boolean {
    const application = checkRequired(params, 'application');

    const bean: DeleteDynamicCmsHandler = __.newBean<DeleteDynamicCmsHandler>('com.enonic.xp.lib.schema.DeleteDynamicCmsHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface Phrases {
    application: string;
    name: string;
    modifiedTime: string;
    resource: string;
}

export interface CreateDynamicPhrasesParams {
    application: string;
    name: string;
    resource: string;
}

interface CreateDynamicPhrasesHandler {
    setApplication(value: string): void;

    setName(value: string): void;

    setResource(value: string): void;

    execute(): Phrases;
}

/**
 * Creates dynamic i18n phrases resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.name Phrases file name without the `.properties` extension, e.g. `phrases` or `phrases_en`.
 * @param {string} params.resource Phrases resource value in the Java properties format.
 *
 * @returns {Phrases} created resource.
 */
export function createPhrases(params: CreateDynamicPhrasesParams): Phrases {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicPhrasesHandler = __.newBean<CreateDynamicPhrasesHandler>('com.enonic.xp.lib.schema.CreateDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicPhrasesParams {
    application: string;
    name: string;
    resource: string;
}

interface UpdateDynamicPhrasesHandler {
    setApplication(value: string): void;

    setName(value: string): void;

    setResource(value: string): void;

    execute(): Phrases;
}

/**
 * Updates dynamic i18n phrases resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.name Phrases file name without the `.properties` extension, e.g. `phrases` or `phrases_en`.
 * @param {string} params.resource Phrases resource value in the Java properties format.
 *
 * @returns {Phrases} updated resource.
 */
export function updatePhrases(params: UpdateDynamicPhrasesParams): Phrases {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicPhrasesHandler = __.newBean<UpdateDynamicPhrasesHandler>('com.enonic.xp.lib.schema.UpdateDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicPhrasesParams {
    application: string;
    name: string;
}

interface GetDynamicPhrasesHandler {
    setApplication(value: string): void;

    setName(value: string): void;

    execute(): Phrases | null;
}

/**
 * Fetches dynamic i18n phrases resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.name Phrases file name without the `.properties` extension, e.g. `phrases` or `phrases_en`.
 *
 * @returns {Phrases | null} fetched resource, or `null` if not found.
 */
export function getPhrases(params: GetDynamicPhrasesParams): Phrases | null {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: GetDynamicPhrasesHandler = __.newBean<GetDynamicPhrasesHandler>('com.enonic.xp.lib.schema.GetDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicPhrasesParams {
    application: string;
}

interface ListDynamicPhrasesHandler {
    setApplication(value: string): void;

    execute(): Phrases[];
}

/**
 * Fetches all dynamic i18n phrases resources of an application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {Phrases[]} fetched resources.
 */
export function listPhrases(params: ListDynamicPhrasesParams): Phrases[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicPhrasesHandler = __.newBean<ListDynamicPhrasesHandler>('com.enonic.xp.lib.schema.ListDynamicPhrasesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicPhrasesParams {
    application: string;
    name: string;
}

interface DeleteDynamicPhrasesHandler {
    setApplication(value: string): void;

    setName(value: string): void;

    execute(): boolean;
}

/**
 * Removes dynamic i18n phrases resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.name Phrases file name without the `.properties` extension, e.g. `phrases` or `phrases_en`.
 *
 * @returns {boolean} `true` if the resource was removed.
 */
export function deletePhrases(params: DeleteDynamicPhrasesParams): boolean {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicPhrasesHandler = __.newBean<DeleteDynamicPhrasesHandler>('com.enonic.xp.lib.schema.DeleteDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicComponentsParams {
    application: string;
}

interface ListDynamicComponentsHandler<T extends ComponentDescriptor> {
    setApplication(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): T[];
}

/**
 * Fetches dynamic part resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {PartDescriptor[]} fetched resources.
 */
export function listParts(params: ListDynamicComponentsParams): PartDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicComponentsHandler<PartDescriptor> = __.newBean<ListDynamicComponentsHandler<PartDescriptor>>('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(application);
    bean.setType('PART');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic layout resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {LayoutDescriptor[]} fetched resources.
 */
export function listLayouts(params: ListDynamicComponentsParams): LayoutDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicComponentsHandler<LayoutDescriptor> = __.newBean<ListDynamicComponentsHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(application);
    bean.setType('LAYOUT');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic page resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {PageDescriptor[]} fetched resources.
 */
export function listPages(params: ListDynamicComponentsParams): PageDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicComponentsHandler<PageDescriptor> = __.newBean<ListDynamicComponentsHandler<PageDescriptor>>('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(application);
    bean.setType('PAGE');
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicSchemasParams {
    application: string;
}

interface ListDynamicSchemasHandler<T extends Schema> {
    setApplication(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): T[];
}

/**
 * Fetches dynamic content type resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {ContentTypeSchema[]} fetched resources.
 */
export function listContentTypes(params: ListDynamicSchemasParams): ContentTypeSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicSchemasHandler<ContentTypeSchema> = __.newBean<ListDynamicSchemasHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(application);
    bean.setType('CONTENT_TYPE');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic form fragment resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {FormFragmentSchema[]} fetched resources.
 */
export function listFormFragments(params: ListDynamicSchemasParams): FormFragmentSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicSchemasHandler<FormFragmentSchema> = __.newBean<ListDynamicSchemasHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(application);
    bean.setType('FORM_FRAGMENT');
    return __.toNativeObject(bean.execute());
}

/**
 * Fetches dynamic mixin resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {MixinSchema[]} fetched resources.
 */
export function listMixins(params: ListDynamicSchemasParams): MixinSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicSchemasHandler<MixinSchema> = __.newBean<ListDynamicSchemasHandler<MixinSchema>>('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(application);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

export interface MacroDescriptor {
    key: string;
    name: string;
    title: string;
    titleI18nKey: string;
    description: string;
    descriptionI18nKey: string;
    modifiedTime: string;
    resource: string;
    form: FormItem[];
    config: Record<string, ConfigValue>;
    icon?: Icon;
}

export interface CreateDynamicMacroParams {
    key: string;
    resource: string;
}

interface CreateDynamicMacroHandler {
    setKey(value: string): void;

    setResource(value: string): void;

    execute(): MacroDescriptor;
}

/**
 * Creates dynamic macro resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro resource descriptor key.
 * @param {string} params.resource Macro resource value.
 *
 * @returns {MacroDescriptor} created resource.
 */
export function createMacro(params: CreateDynamicMacroParams): MacroDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicMacroHandler = __.newBean<CreateDynamicMacroHandler>('com.enonic.xp.lib.schema.CreateDynamicMacroHandler');
    bean.setKey(key);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateDynamicMacroParams {
    key: string;
    resource: string;
}

interface UpdateDynamicMacroHandler {
    setKey(value: string): void;

    setResource(value: string): void;

    execute(): MacroDescriptor;
}

/**
 * Updates dynamic macro resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro resource descriptor key.
 * @param {string} params.resource Macro resource value.
 *
 * @returns {MacroDescriptor} updated resource.
 */
export function updateMacro(params: UpdateDynamicMacroParams): MacroDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicMacroHandler = __.newBean<UpdateDynamicMacroHandler>('com.enonic.xp.lib.schema.UpdateDynamicMacroHandler');
    bean.setKey(key);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicMacroParams {
    key: string;
}

interface GetDynamicMacroHandler {
    setKey(value: string): void;

    execute(): MacroDescriptor | null;
}

/**
 * Fetches dynamic macro resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro resource descriptor key.
 *
 * @returns {MacroDescriptor | null} fetched resource, or `null` if not found.
 */
export function getMacro(params: GetDynamicMacroParams): MacroDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicMacroHandler = __.newBean<GetDynamicMacroHandler>('com.enonic.xp.lib.schema.GetDynamicMacroHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicMacrosParams {
    application: string;
}

interface ListDynamicMacrosHandler {
    setApplication(value: string): void;

    execute(): MacroDescriptor[];
}

/**
 * Fetches dynamic macro resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {MacroDescriptor[]} fetched resources.
 */
export function listMacros(params: ListDynamicMacrosParams): MacroDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicMacrosHandler = __.newBean<ListDynamicMacrosHandler>('com.enonic.xp.lib.schema.ListDynamicMacrosHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteDynamicMacroParams {
    key: string;
}

interface DeleteDynamicMacroHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Removes dynamic macro resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMacro(params: DeleteDynamicMacroParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicMacroHandler = __.newBean<DeleteDynamicMacroHandler>('com.enonic.xp.lib.schema.DeleteDynamicMacroHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface Namespace {
    key: string;
    description?: string;
}

export interface GetNamespaceParams {
    key: string;
}

interface GetNamespaceHandler {
    setKey(value: string): void;

    execute(): Namespace | null;
}

/**
 * Fetches a namespace by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Namespace (application) key.
 *
 * @returns {Namespace | null} fetched namespace, or `null` if not found.
 */
export function getNamespace(params: GetNamespaceParams): Namespace | null {
    const key = checkRequired(params, 'key');

    const bean: GetNamespaceHandler = __.newBean<GetNamespaceHandler>('com.enonic.xp.lib.schema.GetNamespaceHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface CreateNamespaceParams {
    key: string;
    description?: string;
}

interface CreateNamespaceHandler {
    setKey(value: string): void;

    setDescription(value: string): void;

    execute(): Namespace;
}

/**
 * Creates a namespace.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Namespace (application) key.
 * @param {string} [params.description] Namespace description.
 *
 * @returns {Namespace} created namespace.
 */
export function createNamespace(params: CreateNamespaceParams): Namespace {
    const key = checkRequired(params, 'key');

    const bean: CreateNamespaceHandler = __.newBean<CreateNamespaceHandler>('com.enonic.xp.lib.schema.CreateNamespaceHandler');
    bean.setKey(key);
    if (params.description != null) {
        bean.setDescription(params.description);
    }
    return __.toNativeObject(bean.execute());
}

export interface UpdateNamespaceParams {
    key: string;
    description?: string;
}

interface UpdateNamespaceHandler {
    setKey(value: string): void;

    setDescription(value: string): void;

    execute(): Namespace;
}

/**
 * Updates a namespace. The namespace must exist.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Namespace (application) key.
 * @param {string} [params.description] New namespace description. Omit to clear the description.
 *
 * @returns {Namespace} updated namespace.
 */
export function updateNamespace(params: UpdateNamespaceParams): Namespace {
    const key = checkRequired(params, 'key');

    const bean: UpdateNamespaceHandler = __.newBean<UpdateNamespaceHandler>('com.enonic.xp.lib.schema.UpdateNamespaceHandler');
    bean.setKey(key);
    if (params.description != null) {
        bean.setDescription(params.description);
    }
    return __.toNativeObject(bean.execute());
}

export interface DeleteNamespaceParams {
    key: string;
}

interface DeleteNamespaceHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Deletes a namespace.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Namespace (application) key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteNamespace(params: DeleteNamespaceParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteNamespaceHandler = __.newBean<DeleteNamespaceHandler>('com.enonic.xp.lib.schema.DeleteNamespaceHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

interface ListNamespacesHandler {
    execute(): Namespace[];
}

/**
 * Fetches all available namespaces.
 *
 * @returns {Namespace[]} namespaces list.
 */
export function listNamespaces(): Namespace[] {
    const bean: ListNamespacesHandler = __.newBean<ListNamespacesHandler>('com.enonic.xp.lib.schema.ListNamespacesHandler');
    return __.toNativeObject(bean.execute());
}
