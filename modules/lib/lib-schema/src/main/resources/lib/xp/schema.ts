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
    /**
     * @deprecated Not a dependable measure of when the icon last changed, and unsuitable for cache
     * invalidation. Icons are loaded from application resources, so this derives from a jar entry timestamp
     * that build tools normalize to a constant for reproducibility, from the install time of the bundle
     * providing the icon, or from the time the icon happened to be read.
     */
    modifiedTime: string;
}

export type ContentSchemaType = 'CONTENT_TYPE' | 'FORM_FRAGMENT' | 'MIXIN';

export interface CreateContentSchemaParams {
    name: string;
    resource: string;
}

interface CreateContentSchemaHandler<T extends Schema> {
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
    /**
     * @deprecated Not a dependable measure of when this last changed. Read from an application resource,
     * this derives from a jar entry timestamp that build tools normalize to a constant for reproducibility;
     * only a dynamic schema stored in a repository node carries a genuine time, and the two cannot be told
     * apart here.
     */
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
export function createContentType(params: CreateContentSchemaParams): ContentTypeSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateContentSchemaHandler<ContentTypeSchema> = __.newBean<CreateContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.CreateContentSchemaHandler');
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
export function createFormFragment(params: CreateContentSchemaParams): FormFragmentSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateContentSchemaHandler<FormFragmentSchema> = __.newBean<CreateContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.CreateContentSchemaHandler');
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
export function createMixin(params: CreateContentSchemaParams): MixinSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateContentSchemaHandler<MixinSchema> = __.newBean<CreateContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.CreateContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link CreateContentSchemaParams} instead.
 */
export interface CreateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

/**
 * Creates dynamic content schema resource.
 *
 * @deprecated Use {@link createContentType}, {@link createFormFragment} or {@link createMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Schema resource name.
 * @param {string} params.type Schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} created resource.
 */
export function createSchema(params: CreateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'CONTENT_TYPE':
            return createContentType(params);
        case 'FORM_FRAGMENT':
            return createFormFragment(params);
        case 'MIXIN':
            return createMixin(params);
        default:
            throw new Error(`Unsupported schema type: ${String(type)}`);
    }
}

export type ComponentDescriptorType = 'PAGE' | 'LAYOUT' | 'PART';

export interface CreateComponentParams {
    key: string;
    resource: string;
}

interface CreateComponentHandler<T extends ComponentDescriptor> {
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
    /**
     * @deprecated Not a dependable measure of when this last changed. Read from an application resource,
     * this derives from a jar entry timestamp that build tools normalize to a constant for reproducibility;
     * only a dynamic schema stored in a repository node carries a genuine time, and the two cannot be told
     * apart here.
     */
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
export function createPart(params: CreateComponentParams): PartDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateComponentHandler<PartDescriptor> = __.newBean<CreateComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.CreateComponentHandler');

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
export function createLayout(params: CreateComponentParams): LayoutDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateComponentHandler<LayoutDescriptor> = __.newBean<CreateComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.CreateComponentHandler');

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
export function createPage(params: CreateComponentParams): PageDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateComponentHandler<PageDescriptor> = __.newBean<CreateComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.CreateComponentHandler');

    bean.setKey(key);
    bean.setType('PAGE');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link CreateComponentParams} instead.
 */
export interface CreateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

/**
 * Creates dynamic component resource.
 *
 * @deprecated Use {@link createPart}, {@link createLayout} or {@link createPage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} created resource.
 */
export function createComponent(params: CreateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'PART':
            return createPart(params);
        case 'LAYOUT':
            return createLayout(params);
        case 'PAGE':
            return createPage(params);
        default:
            throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

export interface CreateStylesParams {
    application: string;
    resource: string;
}

/**
 * @deprecated Use {@link CreateStylesParams} instead.
 */
export type CreateDynamicStylesParams = CreateStylesParams;

interface CreateStylesHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): StyleDescriptor;
}

export type EditorConfig = ConfigObject & {
    css?: string;
};

export interface StyleDescriptor {
    application: string;
    /**
     * @deprecated Not a dependable measure of when this last changed. Read from an application resource,
     * this derives from a jar entry timestamp that build tools normalize to a constant for reproducibility;
     * only a dynamic schema stored in a repository node carries a genuine time, and the two cannot be told
     * apart here.
     */
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
export function createStyles(params: CreateStylesParams): StyleDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateStylesHandler = __.newBean<CreateStylesHandler>('com.enonic.xp.lib.schema.CreateStylesHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetContentSchemaParams {
    name: string;
}

interface GetContentSchemaHandler<T extends Schema> {
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
export function getContentType(params: GetContentSchemaParams): ContentTypeSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetContentSchemaHandler<ContentTypeSchema> = __.newBean<GetContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.GetContentSchemaHandler');
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
export function getFormFragment(params: GetContentSchemaParams): FormFragmentSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetContentSchemaHandler<FormFragmentSchema> = __.newBean<GetContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.GetContentSchemaHandler');
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
export function getMixin(params: GetContentSchemaParams): MixinSchema | null {
    const name = checkRequired(params, 'name');

    const bean: GetContentSchemaHandler<MixinSchema> = __.newBean<GetContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.GetContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link GetContentSchemaParams} instead.
 */
export interface GetDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
}

/**
 * Fetches dynamic content schema resource.
 *
 * @deprecated Use {@link getContentType}, {@link getFormFragment} or {@link getMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema | null} fetched resource, or `null` if not found.
 */
export function getSchema(params: GetDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema | null {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'CONTENT_TYPE':
            return getContentType(params);
        case 'FORM_FRAGMENT':
            return getFormFragment(params);
        case 'MIXIN':
            return getMixin(params);
        default:
            throw new Error(`Unsupported schema type: ${String(type)}`);
    }
}

export interface GetComponentParams {
    key: string;
}

interface GetComponentHandler<T extends ComponentDescriptor> {
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
export function getPart(params: GetComponentParams): PartDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetComponentHandler<PartDescriptor> = __.newBean<GetComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.GetComponentHandler');
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
export function getLayout(params: GetComponentParams): LayoutDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetComponentHandler<LayoutDescriptor> = __.newBean<GetComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.GetComponentHandler');
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
export function getPage(params: GetComponentParams): PageDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetComponentHandler<PageDescriptor> = __.newBean<GetComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.GetComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link GetComponentParams} instead.
 */
export interface GetDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
}

/**
 * Fetches dynamic component resource.
 *
 * @deprecated Use {@link getPart}, {@link getLayout} or {@link getPage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} fetched resource.
 */
export function getComponent(params: GetDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'PART':
            return getPart(params) as PartDescriptor;
        case 'LAYOUT':
            return getLayout(params) as LayoutDescriptor;
        case 'PAGE':
            return getPage(params) as PageDescriptor;
        default:
            throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

export interface CmsDescriptor {
    application: string;
    resource: string;
    /**
     * @deprecated Not a dependable measure of when this last changed. Read from an application resource,
     * this derives from a jar entry timestamp that build tools normalize to a constant for reproducibility;
     * only a dynamic schema stored in a repository node carries a genuine time, and the two cannot be told
     * apart here.
     */
    modifiedTime: string;
    form: FormItem[];
    mixinMappings?: {
        name: string;
        optional: boolean;
        allowContentTypes: string;
    }[];
}

export interface GetCmsParams {
    application: string;
}

interface GetCmsHandler {
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
export function getCms(params: GetCmsParams): CmsDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetCmsHandler = __.newBean<GetCmsHandler>('com.enonic.xp.lib.schema.GetCmsHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link CmsDescriptor} instead.
 */
export type SiteDescriptor = CmsDescriptor;

/**
 * @deprecated Use {@link GetCmsParams} instead.
 */
export type GetDynamicSiteParams = GetCmsParams;

/**
 * Fetches dynamic site schema resource.
 *
 * @deprecated Use {@link getCms} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {SiteDescriptor | null} fetched resource, or `null` if not found.
 */
export function getSite(params: GetDynamicSiteParams): SiteDescriptor | null {
    return getCms(params);
}

export interface GetStylesParams {
    application: string;
}

/**
 * @deprecated Use {@link GetStylesParams} instead.
 */
export type GetDynamicStylesParams = GetStylesParams;

interface GetStylesHandler {
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
export function getStyles(params: GetStylesParams): StyleDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetStylesHandler = __.newBean<GetStylesHandler>('com.enonic.xp.lib.schema.GetStylesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteContentSchemaParams {
    name: string;
}

interface DeleteContentSchemaHandler {
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
export function deleteContentType(params: DeleteContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteContentSchemaHandler = __.newBean<DeleteContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteContentSchemaHandler');
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
export function deleteFormFragment(params: DeleteContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteContentSchemaHandler = __.newBean<DeleteContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteContentSchemaHandler');
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
export function deleteMixin(params: DeleteContentSchemaParams): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteContentSchemaHandler = __.newBean<DeleteContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link DeleteContentSchemaParams} instead.
 */
export interface DeleteDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
}

/**
 * Removes dynamic schema resource.
 *
 * @deprecated Use {@link deleteContentType}, {@link deleteFormFragment} or {@link deleteMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteSchema(params: DeleteDynamicContentSchemaParams): boolean {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'CONTENT_TYPE':
            return deleteContentType(params);
        case 'FORM_FRAGMENT':
            return deleteFormFragment(params);
        case 'MIXIN':
            return deleteMixin(params);
        default:
            throw new Error(`Unsupported schema type: ${String(type)}`);
    }
}

export interface DeleteComponentParams {
    key: string;
}

interface DeleteComponentHandler {
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
export function deletePart(params: DeleteComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteComponentHandler = __.newBean<DeleteComponentHandler>('com.enonic.xp.lib.schema.DeleteComponentHandler');
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
export function deleteLayout(params: DeleteComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteComponentHandler = __.newBean<DeleteComponentHandler>('com.enonic.xp.lib.schema.DeleteComponentHandler');
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
export function deletePage(params: DeleteComponentParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteComponentHandler = __.newBean<DeleteComponentHandler>('com.enonic.xp.lib.schema.DeleteComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link DeleteComponentParams} instead.
 */
export interface DeleteDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
}

/**
 * Removes dynamic component resource.
 *
 * @deprecated Use {@link deletePart}, {@link deleteLayout} or {@link deletePage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteComponent(params: DeleteDynamicComponentParams): boolean {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'PART':
            return deletePart(params);
        case 'LAYOUT':
            return deleteLayout(params);
        case 'PAGE':
            return deletePage(params);
        default:
            throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

export interface DeleteStylesParams {
    application: string;
}

/**
 * @deprecated Use {@link DeleteStylesParams} instead.
 */
export type DeleteDynamicStylesParams = DeleteStylesParams;

interface DeleteStylesHandler {
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
export function deleteStyles(params: DeleteStylesParams): boolean {
    const application = checkRequired(params, 'application');

    const bean: DeleteStylesHandler = __.newBean<DeleteStylesHandler>('com.enonic.xp.lib.schema.DeleteStylesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface UpdateContentSchemaParams {
    name: string;
    resource: string;
}

interface UpdateContentSchemaHandler<T extends Schema> {
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
export function updateContentType(params: UpdateContentSchemaParams): ContentTypeSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateContentSchemaHandler<ContentTypeSchema> = __.newBean<UpdateContentSchemaHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.UpdateContentSchemaHandler');
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
export function updateFormFragment(params: UpdateContentSchemaParams): FormFragmentSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateContentSchemaHandler<FormFragmentSchema> = __.newBean<UpdateContentSchemaHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.UpdateContentSchemaHandler');
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
export function updateMixin(params: UpdateContentSchemaParams): MixinSchema {
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateContentSchemaHandler<MixinSchema> = __.newBean<UpdateContentSchemaHandler<MixinSchema>>('com.enonic.xp.lib.schema.UpdateContentSchemaHandler');
    bean.setName(name);
    bean.setType('MIXIN');
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link UpdateContentSchemaParams} instead.
 */
export interface UpdateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

/**
 * Updates dynamic content schema resource.
 *
 * @deprecated Use {@link updateContentType}, {@link updateFormFragment} or {@link updateMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} updated resource.
 */
export function updateSchema(params: UpdateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'CONTENT_TYPE':
            return updateContentType(params);
        case 'FORM_FRAGMENT':
            return updateFormFragment(params);
        case 'MIXIN':
            return updateMixin(params);
        default:
            throw new Error(`Unsupported schema type: ${String(type)}`);
    }
}

export interface UpdateComponentParams {
    key: string;
    resource: string;
}

interface UpdateComponentHandler<T extends ComponentDescriptor> {
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
export function updatePart(params: UpdateComponentParams): PartDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateComponentHandler<PartDescriptor> = __.newBean<UpdateComponentHandler<PartDescriptor>>('com.enonic.xp.lib.schema.UpdateComponentHandler');
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
export function updateLayout(params: UpdateComponentParams): LayoutDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateComponentHandler<LayoutDescriptor> = __.newBean<UpdateComponentHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.UpdateComponentHandler');
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
export function updatePage(params: UpdateComponentParams): PageDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateComponentHandler<PageDescriptor> = __.newBean<UpdateComponentHandler<PageDescriptor>>('com.enonic.xp.lib.schema.UpdateComponentHandler');
    bean.setKey(key);
    bean.setType('PAGE');
    bean.setResource(resource);

    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link UpdateComponentParams} instead.
 */
export interface UpdateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

/**
 * Updates dynamic component resource.
 *
 * @deprecated Use {@link updatePart}, {@link updateLayout} or {@link updatePage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} updated resource.
 */
export function updateComponent(params: UpdateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'PART':
            return updatePart(params);
        case 'LAYOUT':
            return updateLayout(params);
        case 'PAGE':
            return updatePage(params);
        default:
            throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

export interface CreateCmsParams {
    application: string;
    resource: string;
}

interface CreateCmsHandler {
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
export function createCms(params: CreateCmsParams): CmsDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateCmsHandler = __.newBean<CreateCmsHandler>('com.enonic.xp.lib.schema.CreateCmsHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateCmsParams {
    application: string;
    resource: string;
}

interface UpdateCmsHandler {
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
export function updateCms(params: UpdateCmsParams): CmsDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateCmsHandler = __.newBean<UpdateCmsHandler>('com.enonic.xp.lib.schema.UpdateCmsHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link UpdateCmsParams} instead.
 */
export type UpdateDynamicSiteParams = UpdateCmsParams;

/**
 * Updates dynamic site schema resource.
 *
 * @deprecated Use {@link updateCms} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Site schema resource value.
 *
 * @returns {SiteDescriptor} updated resource.
 */
export function updateSite(params: UpdateDynamicSiteParams): SiteDescriptor {
    return updateCms(params);
}

export interface UpdateStylesParams {
    application: string;
    resource: string;
}

/**
 * @deprecated Use {@link UpdateStylesParams} instead.
 */
export type UpdateDynamicStylesParams = UpdateStylesParams;

interface UpdateStylesHandler {
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
export function updateStyles(params: UpdateStylesParams): StyleDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateStylesHandler = __.newBean<UpdateStylesHandler>('com.enonic.xp.lib.schema.UpdateStylesHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface DeleteCmsParams {
    application: string;
}

interface DeleteCmsHandler {
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
export function deleteCms(params: DeleteCmsParams): boolean {
    const application = checkRequired(params, 'application');

    const bean: DeleteCmsHandler = __.newBean<DeleteCmsHandler>('com.enonic.xp.lib.schema.DeleteCmsHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface Phrases {
    application: string;
    name: string;
    modifiedTime: string;
    resource: string;
}

export interface CreatePhrasesParams {
    application: string;
    name: string;
    resource: string;
}

interface CreatePhrasesHandler {
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
export function createPhrases(params: CreatePhrasesParams): Phrases {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreatePhrasesHandler = __.newBean<CreatePhrasesHandler>('com.enonic.xp.lib.schema.CreatePhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdatePhrasesParams {
    application: string;
    name: string;
    resource: string;
}

interface UpdatePhrasesHandler {
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
export function updatePhrases(params: UpdatePhrasesParams): Phrases {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: UpdatePhrasesHandler = __.newBean<UpdatePhrasesHandler>('com.enonic.xp.lib.schema.UpdatePhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetPhrasesParams {
    application: string;
    name: string;
}

interface GetPhrasesHandler {
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
export function getPhrases(params: GetPhrasesParams): Phrases | null {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: GetPhrasesHandler = __.newBean<GetPhrasesHandler>('com.enonic.xp.lib.schema.GetPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListPhrasesParams {
    application: string;
}

interface ListPhrasesHandler {
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
export function listPhrases(params: ListPhrasesParams): Phrases[] {
    const application = checkRequired(params, 'application');

    const bean: ListPhrasesHandler = __.newBean<ListPhrasesHandler>('com.enonic.xp.lib.schema.ListPhrasesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeletePhrasesParams {
    application: string;
    name: string;
}

interface DeletePhrasesHandler {
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
export function deletePhrases(params: DeletePhrasesParams): boolean {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: DeletePhrasesHandler = __.newBean<DeletePhrasesHandler>('com.enonic.xp.lib.schema.DeletePhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListComponentsParams {
    application: string;
}

interface ListComponentsHandler<T extends ComponentDescriptor> {
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
export function listParts(params: ListComponentsParams): PartDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListComponentsHandler<PartDescriptor> = __.newBean<ListComponentsHandler<PartDescriptor>>('com.enonic.xp.lib.schema.ListComponentsHandler');
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
export function listLayouts(params: ListComponentsParams): LayoutDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListComponentsHandler<LayoutDescriptor> = __.newBean<ListComponentsHandler<LayoutDescriptor>>('com.enonic.xp.lib.schema.ListComponentsHandler');
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
export function listPages(params: ListComponentsParams): PageDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListComponentsHandler<PageDescriptor> = __.newBean<ListComponentsHandler<PageDescriptor>>('com.enonic.xp.lib.schema.ListComponentsHandler');
    bean.setApplication(application);
    bean.setType('PAGE');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link ListComponentsParams} instead.
 */
export interface ListDynamicComponentsParams {
    application: string;
    type: ComponentDescriptorType;
}

/**
 * Fetches dynamic component resources.
 *
 * @deprecated Use {@link listParts}, {@link listLayouts} or {@link listPages} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.type Component type.
 *
 * @returns {PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[]} fetched resources.
 */
export function listComponents(params: ListDynamicComponentsParams): PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[] {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'PART':
            return listParts(params);
        case 'LAYOUT':
            return listLayouts(params);
        case 'PAGE':
            return listPages(params);
        default:
            throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

export interface ListSchemasParams {
    application: string;
}

interface ListSchemasHandler<T extends Schema> {
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
export function listContentTypes(params: ListSchemasParams): ContentTypeSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListSchemasHandler<ContentTypeSchema> = __.newBean<ListSchemasHandler<ContentTypeSchema>>('com.enonic.xp.lib.schema.ListSchemasHandler');
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
export function listFormFragments(params: ListSchemasParams): FormFragmentSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListSchemasHandler<FormFragmentSchema> = __.newBean<ListSchemasHandler<FormFragmentSchema>>('com.enonic.xp.lib.schema.ListSchemasHandler');
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
export function listMixins(params: ListSchemasParams): MixinSchema[] {
    const application = checkRequired(params, 'application');

    const bean: ListSchemasHandler<MixinSchema> = __.newBean<ListSchemasHandler<MixinSchema>>('com.enonic.xp.lib.schema.ListSchemasHandler');
    bean.setApplication(application);
    bean.setType('MIXIN');
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link ListSchemasParams} instead.
 */
export interface ListDynamicSchemasParams {
    application: string;
    type: ContentSchemaType;
}

/**
 * Fetches dynamic content schemas resources.
 *
 * @deprecated Use {@link listContentTypes}, {@link listFormFragments} or {@link listMixins} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[]} fetched resources.
 */
export function listSchemas(params: ListDynamicSchemasParams): ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[] {
    const type = checkRequired(params, 'type');

    switch (type) {
        case 'CONTENT_TYPE':
            return listContentTypes(params);
        case 'FORM_FRAGMENT':
            return listFormFragments(params);
        case 'MIXIN':
            return listMixins(params);
        default:
            throw new Error(`Unsupported schema type: ${String(type)}`);
    }
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

export interface CreateMacroParams {
    key: string;
    resource: string;
}

interface CreateMacroHandler {
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
export function createMacro(params: CreateMacroParams): MacroDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: CreateMacroHandler = __.newBean<CreateMacroHandler>('com.enonic.xp.lib.schema.CreateMacroHandler');
    bean.setKey(key);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface UpdateMacroParams {
    key: string;
    resource: string;
}

interface UpdateMacroHandler {
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
export function updateMacro(params: UpdateMacroParams): MacroDescriptor {
    const key = checkRequired(params, 'key');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateMacroHandler = __.newBean<UpdateMacroHandler>('com.enonic.xp.lib.schema.UpdateMacroHandler');
    bean.setKey(key);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetMacroParams {
    key: string;
}

interface GetMacroHandler {
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
export function getMacro(params: GetMacroParams): MacroDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetMacroHandler = __.newBean<GetMacroHandler>('com.enonic.xp.lib.schema.GetMacroHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface ListMacrosParams {
    application: string;
}

interface ListMacrosHandler {
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
export function listMacros(params: ListMacrosParams): MacroDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListMacrosHandler = __.newBean<ListMacrosHandler>('com.enonic.xp.lib.schema.ListMacrosHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteMacroParams {
    key: string;
}

interface DeleteMacroHandler {
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
export function deleteMacro(params: DeleteMacroParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteMacroHandler = __.newBean<DeleteMacroHandler>('com.enonic.xp.lib.schema.DeleteMacroHandler');
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

type IconType = ContentSchemaType | 'PART' | 'MACRO';

export interface SetSchemaIconParams {
    name: string;
    data: ByteSource;
    mimeType: string;
}

export interface SetComponentIconParams {
    key: string;
    data: ByteSource;
    mimeType: string;
}

export interface SetMacroIconParams {
    key: string;
    data: ByteSource;
    mimeType: string;
}

export interface SchemaIconParams {
    name: string;
}

export interface ComponentIconParams {
    key: string;
}

export interface MacroIconParams {
    key: string;
}

interface SetIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    setData(value: ByteSource): void;

    setMimeType(value: string): void;

    execute(): Icon;
}

interface GetIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    execute(): Icon | null;
}

interface DeleteIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    execute(): boolean;
}

function doSetIcon(name: string, type: IconType, data: ByteSource, mimeType: string): Icon {
    const bean: SetIconHandler = __.newBean<SetIconHandler>('com.enonic.xp.lib.schema.SetIconHandler');
    bean.setName(name);
    bean.setType(type);
    bean.setData(data);
    bean.setMimeType(mimeType);
    return __.toNativeObject(bean.execute());
}

function doGetIcon(name: string, type: IconType): Icon | null {
    const bean: GetIconHandler = __.newBean<GetIconHandler>('com.enonic.xp.lib.schema.GetIconHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

function doDeleteIcon(name: string, type: IconType): boolean {
    const bean: DeleteIconHandler = __.newBean<DeleteIconHandler>('com.enonic.xp.lib.schema.DeleteIconHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

/**
 * Sets an icon for a dynamic content type. Replaces the existing icon, if any.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type name.
 * @param {object} params.data Icon image data stream. SVG or PNG.
 * @param {string} params.mimeType Icon mime type: `image/svg+xml` or `image/png`.
 *
 * @returns {Icon} stored icon.
 */
export function setContentTypeIcon(params: SetSchemaIconParams): Icon {
    return doSetIcon(checkRequired(params, 'name'), 'CONTENT_TYPE', checkRequired(params, 'data'), checkRequired(params, 'mimeType'));
}

/**
 * Fetches an icon of a dynamic content type.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type name.
 *
 * @returns {Icon | null} icon, or null if not found.
 */
export function getContentTypeIcon(params: SchemaIconParams): Icon | null {
    return doGetIcon(checkRequired(params, 'name'), 'CONTENT_TYPE');
}

/**
 * Deletes an icon of a dynamic content type.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteContentTypeIcon(params: SchemaIconParams): boolean {
    return doDeleteIcon(checkRequired(params, 'name'), 'CONTENT_TYPE');
}

/**
 * Sets an icon for a dynamic form fragment. Replaces the existing icon, if any.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment name.
 * @param {object} params.data Icon image data stream. SVG or PNG.
 * @param {string} params.mimeType Icon mime type: `image/svg+xml` or `image/png`.
 *
 * @returns {Icon} stored icon.
 */
export function setFormFragmentIcon(params: SetSchemaIconParams): Icon {
    return doSetIcon(checkRequired(params, 'name'), 'FORM_FRAGMENT', checkRequired(params, 'data'), checkRequired(params, 'mimeType'));
}

/**
 * Fetches an icon of a dynamic form fragment.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment name.
 *
 * @returns {Icon | null} icon, or null if not found.
 */
export function getFormFragmentIcon(params: SchemaIconParams): Icon | null {
    return doGetIcon(checkRequired(params, 'name'), 'FORM_FRAGMENT');
}

/**
 * Deletes an icon of a dynamic form fragment.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteFormFragmentIcon(params: SchemaIconParams): boolean {
    return doDeleteIcon(checkRequired(params, 'name'), 'FORM_FRAGMENT');
}

/**
 * Sets an icon for a dynamic mixin. Replaces the existing icon, if any.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin name.
 * @param {object} params.data Icon image data stream. SVG or PNG.
 * @param {string} params.mimeType Icon mime type: `image/svg+xml` or `image/png`.
 *
 * @returns {Icon} stored icon.
 */
export function setMixinIcon(params: SetSchemaIconParams): Icon {
    return doSetIcon(checkRequired(params, 'name'), 'MIXIN', checkRequired(params, 'data'), checkRequired(params, 'mimeType'));
}

/**
 * Fetches an icon of a dynamic mixin.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin name.
 *
 * @returns {Icon | null} icon, or null if not found.
 */
export function getMixinIcon(params: SchemaIconParams): Icon | null {
    return doGetIcon(checkRequired(params, 'name'), 'MIXIN');
}

/**
 * Deletes an icon of a dynamic mixin.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMixinIcon(params: SchemaIconParams): boolean {
    return doDeleteIcon(checkRequired(params, 'name'), 'MIXIN');
}

/**
 * Sets an icon for a dynamic part. Replaces the existing icon, if any.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part descriptor key.
 * @param {object} params.data Icon image data stream. SVG or PNG.
 * @param {string} params.mimeType Icon mime type: `image/svg+xml` or `image/png`.
 *
 * @returns {Icon} stored icon.
 */
export function setPartIcon(params: SetComponentIconParams): Icon {
    return doSetIcon(checkRequired(params, 'key'), 'PART', checkRequired(params, 'data'), checkRequired(params, 'mimeType'));
}

/**
 * Fetches an icon of a dynamic part.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part descriptor key.
 *
 * @returns {Icon | null} icon, or null if not found.
 */
export function getPartIcon(params: ComponentIconParams): Icon | null {
    return doGetIcon(checkRequired(params, 'key'), 'PART');
}

/**
 * Deletes an icon of a dynamic part.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deletePartIcon(params: ComponentIconParams): boolean {
    return doDeleteIcon(checkRequired(params, 'key'), 'PART');
}

/**
 * Sets an icon for a dynamic macro. Replaces the existing icon, if any.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro key.
 * @param {object} params.data Icon image data stream. SVG or PNG.
 * @param {string} params.mimeType Icon mime type: `image/svg+xml` or `image/png`.
 *
 * @returns {Icon} stored icon.
 */
export function setMacroIcon(params: SetMacroIconParams): Icon {
    return doSetIcon(checkRequired(params, 'key'), 'MACRO', checkRequired(params, 'data'), checkRequired(params, 'mimeType'));
}

/**
 * Fetches an icon of a dynamic macro.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro key.
 *
 * @returns {Icon | null} icon, or null if not found.
 */
export function getMacroIcon(params: MacroIconParams): Icon | null {
    return doGetIcon(checkRequired(params, 'key'), 'MACRO');
}

/**
 * Deletes an icon of a dynamic macro.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMacroIcon(params: MacroIconParams): boolean {
    return doDeleteIcon(checkRequired(params, 'key'), 'MACRO');
}
