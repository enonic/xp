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

/**
 * An icon to store along with a descriptor: `image/svg+xml` or `image/png`, at most 100 KB.
 */
export interface IconInput {
    data: ByteSource;
    mimeType: string;
}

interface IconAwareHandler {
    setIconData(value: ByteSource): void;

    setIconMimeType(value: string): void;

    setRemoveIcon(value: boolean): void;
}

// the icon left out keeps the stored one, `null` removes it, `{data, mimeType}` replaces it
function applyIcon(bean: IconAwareHandler, icon: IconInput | null | undefined): void {
    if (icon === null) {
        bean.setRemoveIcon(true);
    } else if (icon !== undefined) {
        bean.setIconData(checkRequired(icon, 'data'));
        bean.setIconMimeType(checkRequired(icon, 'mimeType'));
    }
}

export type ContentSchemaType = 'CONTENT_TYPE' | 'FORM_FRAGMENT' | 'MIXIN';

export interface CreateOrUpdateContentSchemaParams {
    name: string;
    resource?: string;
    icon?: IconInput | null;
}

interface CreateOrUpdateContentSchemaHandler<T extends Schema>
    extends IconAwareHandler {
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

function doCreateOrUpdateContentSchema<T extends Schema>(params: CreateOrUpdateContentSchemaParams, type: ContentSchemaType): T {
    const name = checkRequired(params, 'name');

    const bean: CreateOrUpdateContentSchemaHandler<T> = __.newBean<CreateOrUpdateContentSchemaHandler<T>>(
        'com.enonic.xp.lib.schema.CreateOrUpdateContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    if (params.resource != null) {
        bean.setResource(params.resource);
    }
    applyIcon(bean, params.icon);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates the dynamic content type, or updates it when there is one, with its icon.
 *
 * `resource` is required to create the content type; on update it may be left out to change only the icon.
 * The icon is kept when `icon` is left out, removed when `null`, and replaced when `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type name.
 * @param {string} [params.resource] Content type resource value.
 * @param {object|null} [params.icon] Icon to store, or `null` to remove the stored icon.
 *
 * @returns {ContentTypeSchema} the stored content type.
 */
export function createOrUpdateContentType(params: CreateOrUpdateContentSchemaParams): ContentTypeSchema {
    return doCreateOrUpdateContentSchema(params, 'CONTENT_TYPE');
}

/**
 * Creates the dynamic form fragment, or updates it when there is one, with its icon.
 *
 * `resource` is required to create the form fragment; on update it may be left out to change only the icon.
 * The icon is kept when `icon` is left out, removed when `null`, and replaced when `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment name.
 * @param {string} [params.resource] Form fragment resource value.
 * @param {object|null} [params.icon] Icon to store, or `null` to remove the stored icon.
 *
 * @returns {FormFragmentSchema} the stored form fragment.
 */
export function createOrUpdateFormFragment(params: CreateOrUpdateContentSchemaParams): FormFragmentSchema {
    return doCreateOrUpdateContentSchema(params, 'FORM_FRAGMENT');
}

/**
 * Creates the dynamic mixin, or updates it when there is one, with its icon.
 *
 * `resource` is required to create the mixin; on update it may be left out to change only the icon.
 * The icon is kept when `icon` is left out, removed when `null`, and replaced when `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin name.
 * @param {string} [params.resource] Mixin resource value.
 * @param {object|null} [params.icon] Icon to store, or `null` to remove the stored icon.
 *
 * @returns {MixinSchema} the stored mixin.
 */
export function createOrUpdateMixin(params: CreateOrUpdateContentSchemaParams): MixinSchema {
    return doCreateOrUpdateContentSchema(params, 'MIXIN');
}

/**
 * @deprecated Use {@link CreateOrUpdateContentSchemaParams} instead.
 */
export interface CreateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

function dispatchContentSchema<T>(type: ContentSchemaType, contentType: () => T, formFragment: () => T, mixin: () => T): T {
    switch (type) {
    case 'CONTENT_TYPE':
        return contentType();
    case 'FORM_FRAGMENT':
        return formFragment();
    case 'MIXIN':
        return mixin();
    default:
        throw new Error(`Unsupported schema type: ${String(type)}`);
    }
}

/**
 * Creates dynamic content schema resource, or updates it when there is one.
 *
 * @deprecated Use {@link createOrUpdateContentType}, {@link createOrUpdateFormFragment} or {@link createOrUpdateMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Schema resource name.
 * @param {string} params.type Schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} created or updated resource.
 */
export function createSchema(params: CreateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    const type = checkRequired(params, 'type');
    checkRequired(params, 'resource');

    return dispatchContentSchema<ContentTypeSchema | FormFragmentSchema | MixinSchema>(type,
        () => createOrUpdateContentType(params),
        () => createOrUpdateFormFragment(params),
        () => createOrUpdateMixin(params));
}

export type ComponentDescriptorType = 'PAGE' | 'LAYOUT' | 'PART';

export interface CreateOrUpdateComponentParams {
    key: string;
    resource: string;
}

export interface CreateOrUpdatePartParams {
    key: string;
    resource?: string;
    icon?: IconInput | null;
}

interface CreateOrUpdateComponentHandler<T extends ComponentDescriptor>
    extends IconAwareHandler {
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

function doCreateOrUpdateComponent<T extends ComponentDescriptor>(params: CreateOrUpdatePartParams, type: ComponentDescriptorType): T {
    const key = checkRequired(params, 'key');

    const bean: CreateOrUpdateComponentHandler<T> = __.newBean<CreateOrUpdateComponentHandler<T>>(
        'com.enonic.xp.lib.schema.CreateOrUpdateComponentHandler');
    bean.setKey(key);
    bean.setType(type);
    if (params.resource != null) {
        bean.setResource(params.resource);
    }
    applyIcon(bean, params.icon);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates the dynamic part, or updates it when there is one, with its icon.
 *
 * `resource` is required to create the part; on update it may be left out to change only the icon.
 * The icon is kept when `icon` is left out, removed when `null`, and replaced when `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part descriptor key.
 * @param {string} [params.resource] Part resource value.
 * @param {object|null} [params.icon] Icon to store, or `null` to remove the stored icon.
 *
 * @returns {PartDescriptor} the stored part.
 */
export function createOrUpdatePart(params: CreateOrUpdatePartParams): PartDescriptor {
    return doCreateOrUpdateComponent(params, 'PART');
}

/**
 * Creates the dynamic layout, or updates it when there is one.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Layout descriptor key.
 * @param {string} params.resource Layout resource value.
 *
 * @returns {LayoutDescriptor} the stored layout.
 */
export function createOrUpdateLayout(params: CreateOrUpdateComponentParams): LayoutDescriptor {
    checkRequired(params, 'resource');
    return doCreateOrUpdateComponent(params, 'LAYOUT');
}

/**
 * Creates the dynamic page, or updates it when there is one.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Page descriptor key.
 * @param {string} params.resource Page resource value.
 *
 * @returns {PageDescriptor} the stored page.
 */
export function createOrUpdatePage(params: CreateOrUpdateComponentParams): PageDescriptor {
    checkRequired(params, 'resource');
    return doCreateOrUpdateComponent(params, 'PAGE');
}

/**
 * @deprecated Use {@link CreateOrUpdatePartParams} or {@link CreateOrUpdateComponentParams} instead.
 */
export interface CreateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

function dispatchComponent<T>(type: ComponentDescriptorType, part: () => T, layout: () => T, page: () => T): T {
    switch (type) {
    case 'PART':
        return part();
    case 'LAYOUT':
        return layout();
    case 'PAGE':
        return page();
    default:
        throw new Error(`Unsupported component type: ${String(type)}`);
    }
}

/**
 * Creates dynamic component resource, or updates it when there is one.
 *
 * @deprecated Use {@link createOrUpdatePart}, {@link createOrUpdateLayout} or {@link createOrUpdatePage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} created or updated resource.
 */
export function createComponent(params: CreateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const type = checkRequired(params, 'type');
    checkRequired(params, 'resource');

    return dispatchComponent<LayoutDescriptor | PageDescriptor | PartDescriptor>(type,
        () => createOrUpdatePart(params),
        () => createOrUpdateLayout(params),
        () => createOrUpdatePage(params));
}

export interface CreateOrUpdateStylesParams {
    application: string;
    resource: string;
}

/**
 * @deprecated Use {@link CreateOrUpdateStylesParams} instead.
 */
export type CreateDynamicStylesParams = CreateOrUpdateStylesParams;

interface CreateOrUpdateStylesHandler {
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
 * Creates the dynamic styles schema resource of the application, or updates it when there is one.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Styles resource value.
 *
 * @returns {StyleDescriptor} the stored resource.
 */
export function createOrUpdateStyles(params: CreateOrUpdateStylesParams): StyleDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateOrUpdateStylesHandler = __.newBean<CreateOrUpdateStylesHandler>(
        'com.enonic.xp.lib.schema.CreateOrUpdateStylesHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

/**
 * Creates dynamic styles schema resource, or updates it when there is one.
 *
 * @deprecated Use {@link createOrUpdateStyles} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Styles resource value.
 *
 * @returns {StyleDescriptor} created or updated resource.
 */
export function createStyles(params: CreateDynamicStylesParams): StyleDescriptor {
    return createOrUpdateStyles(params);
}

export interface CreateOrUpdateCmsParams {
    application: string;
    resource: string;
}

interface CreateOrUpdateCmsHandler {
    setApplication(value: string): void;

    setResource(value: string): void;

    execute(): CmsDescriptor;
}

/**
 * Creates the dynamic CMS schema resource of the application, or updates it when there is one.
 *
 * The CMS descriptor marks the application as providing schemas for the CMS: content types, components, macros, styles and
 * phrases can be stored for the application once it exists. The application must exist, installed or created through
 * the App library.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource CMS schema resource value.
 *
 * @returns {CmsDescriptor} created or updated resource.
 */
export function createOrUpdateCms(params: CreateOrUpdateCmsParams): CmsDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: CreateOrUpdateCmsHandler = __.newBean<CreateOrUpdateCmsHandler>('com.enonic.xp.lib.schema.CreateOrUpdateCmsHandler');
    bean.setApplication(application);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetContentSchemaParams {
    name: string;
}

interface GetDynamicContentSchemaHandler<T extends Schema> {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): T | null;
}

function doGetContentSchema<T extends Schema>(params: GetContentSchemaParams, type: ContentSchemaType): T | null {
    const name = checkRequired(params, 'name');

    const bean: GetDynamicContentSchemaHandler<T> = __.newBean<GetDynamicContentSchemaHandler<T>>(
        'com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
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
    return doGetContentSchema(params, 'CONTENT_TYPE');
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
    return doGetContentSchema(params, 'FORM_FRAGMENT');
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
    return doGetContentSchema(params, 'MIXIN');
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

    return dispatchContentSchema<ContentTypeSchema | FormFragmentSchema | MixinSchema | null>(type,
        () => getContentType(params),
        () => getFormFragment(params),
        () => getMixin(params));
}

export interface GetComponentParams {
    key: string;
}

interface GetDynamicComponentHandler<T extends ComponentDescriptor> {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): T | null;
}

function doGetComponent<T extends ComponentDescriptor>(params: GetComponentParams, type: ComponentDescriptorType): T | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicComponentHandler<T> = __.newBean<GetDynamicComponentHandler<T>>(
        'com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(key);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
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
    return doGetComponent(params, 'PART');
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
    return doGetComponent(params, 'LAYOUT');
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
    return doGetComponent(params, 'PAGE');
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

    return dispatchComponent<LayoutDescriptor | PageDescriptor | PartDescriptor>(type,
        () => getPart(params) as PartDescriptor,
        () => getLayout(params) as LayoutDescriptor,
        () => getPage(params) as PageDescriptor);
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
export function getCms(params: GetCmsParams): CmsDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetDynamicCmsHandler = __.newBean<GetDynamicCmsHandler>('com.enonic.xp.lib.schema.GetDynamicCmsHandler');
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
export function getStyles(params: GetStylesParams): StyleDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetDynamicStylesHandler = __.newBean<GetDynamicStylesHandler>('com.enonic.xp.lib.schema.GetDynamicStylesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteContentSchemaParams {
    name: string;
}

interface DeleteDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): boolean;
}

function doDeleteContentSchema(params: DeleteContentSchemaParams, type: ContentSchemaType): boolean {
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicContentSchemaHandler = __.newBean<DeleteDynamicContentSchemaHandler>(
        'com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic content type resource, with its icon.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content type resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteContentType(params: DeleteContentSchemaParams): boolean {
    return doDeleteContentSchema(params, 'CONTENT_TYPE');
}

/**
 * Removes dynamic form fragment resource, with its icon.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Form fragment resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteFormFragment(params: DeleteContentSchemaParams): boolean {
    return doDeleteContentSchema(params, 'FORM_FRAGMENT');
}

/**
 * Removes dynamic mixin resource, with its icon.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Mixin resource name.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMixin(params: DeleteContentSchemaParams): boolean {
    return doDeleteContentSchema(params, 'MIXIN');
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

    return dispatchContentSchema<boolean>(type,
        () => deleteContentType(params),
        () => deleteFormFragment(params),
        () => deleteMixin(params));
}

export interface DeleteComponentParams {
    key: string;
}

interface DeleteDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): boolean;
}

function doDeleteComponent(params: DeleteComponentParams, type: ComponentDescriptorType): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicComponentHandler = __.newBean<DeleteDynamicComponentHandler>(
        'com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(key);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

/**
 * Removes dynamic part resource, with its icon.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Part resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deletePart(params: DeleteComponentParams): boolean {
    return doDeleteComponent(params, 'PART');
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
    return doDeleteComponent(params, 'LAYOUT');
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
    return doDeleteComponent(params, 'PAGE');
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

    return dispatchComponent<boolean>(type,
        () => deletePart(params),
        () => deleteLayout(params),
        () => deletePage(params));
}

export interface DeleteStylesParams {
    application: string;
}

/**
 * @deprecated Use {@link DeleteStylesParams} instead.
 */
export type DeleteDynamicStylesParams = DeleteStylesParams;

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
export function deleteStyles(params: DeleteStylesParams): boolean {
    const application = checkRequired(params, 'application');

    const bean: DeleteDynamicStylesHandler = __.newBean<DeleteDynamicStylesHandler>('com.enonic.xp.lib.schema.DeleteDynamicStylesHandler');
    bean.setApplication(application);
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
 * Removes the dynamic CMS schema of the application: the CMS descriptor together with every schema, component, macro,
 * style, phrases file and icon stored for the application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteCms(params: DeleteCmsParams): boolean {
    const application = checkRequired(params, 'application');

    const bean: DeleteCmsHandler = __.newBean<DeleteCmsHandler>('com.enonic.xp.lib.schema.DeleteCmsHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

/**
 * @deprecated Use {@link CreateOrUpdateContentSchemaParams} instead.
 */
export interface UpdateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

/**
 * Updates dynamic content schema resource, or creates it when there is none.
 *
 * @deprecated Use {@link createOrUpdateContentType}, {@link createOrUpdateFormFragment} or {@link createOrUpdateMixin} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} updated or created resource.
 */
export function updateSchema(params: UpdateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    return createSchema(params);
}

/**
 * @deprecated Use {@link CreateOrUpdatePartParams} or {@link CreateOrUpdateComponentParams} instead.
 */
export interface UpdateDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
    resource: string;
}

/**
 * Updates dynamic component resource, or creates it when there is none.
 *
 * @deprecated Use {@link createOrUpdatePart}, {@link createOrUpdateLayout} or {@link createOrUpdatePage} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} updated or created resource.
 */
export function updateComponent(params: UpdateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    return createComponent(params);
}

/**
 * @deprecated Use {@link CreateOrUpdateCmsParams} instead.
 */
export type UpdateCmsParams = CreateOrUpdateCmsParams;

/**
 * Creates or updates dynamic CMS schema resource.
 *
 * @deprecated Use {@link createOrUpdateCms} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource CMS schema resource value.
 *
 * @returns {CmsDescriptor} created or updated resource.
 */
export function updateCms(params: UpdateCmsParams): CmsDescriptor {
    return createOrUpdateCms(params);
}

/**
 * @deprecated Use {@link CreateOrUpdateCmsParams} instead.
 */
export type UpdateDynamicSiteParams = CreateOrUpdateCmsParams;

/**
 * Creates or updates dynamic site schema resource.
 *
 * @deprecated Use {@link createOrUpdateCms} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Site schema resource value.
 *
 * @returns {SiteDescriptor} created or updated resource.
 */
export function updateSite(params: UpdateDynamicSiteParams): SiteDescriptor {
    return createOrUpdateCms(params);
}

/**
 * @deprecated Use {@link CreateOrUpdateStylesParams} instead.
 */
export type UpdateDynamicStylesParams = CreateOrUpdateStylesParams;

/**
 * Updates dynamic styles schema resource, or creates it when there is none.
 *
 * @deprecated Use {@link createOrUpdateStyles} instead.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.resource Styles schema resource value.
 *
 * @returns {StyleDescriptor} updated or created resource.
 */
export function updateStyles(params: UpdateDynamicStylesParams): StyleDescriptor {
    return createOrUpdateStyles(params);
}

export interface Phrases {
    application: string;
    name: string;
    modifiedTime: string;
    resource: string;
}

export interface CreateOrUpdatePhrasesParams {
    application: string;
    name: string;
    resource: string;
}

interface CreateOrUpdatePhrasesHandler {
    setApplication(value: string): void;

    setName(value: string): void;

    setResource(value: string): void;

    execute(): Phrases;
}

/**
 * Creates the dynamic i18n phrases resource, or updates it when there is one.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.name Phrases file name without the `.properties` extension, e.g. `phrases` or `phrases_en`.
 * @param {string} params.resource Phrases resource value in the Java properties format.
 *
 * @returns {Phrases} the stored resource.
 */
export function createOrUpdatePhrases(params: CreateOrUpdatePhrasesParams): Phrases {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');
    const resource = checkRequired(params, 'resource');

    const bean: CreateOrUpdatePhrasesHandler = __.newBean<CreateOrUpdatePhrasesHandler>(
        'com.enonic.xp.lib.schema.CreateOrUpdatePhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    bean.setResource(resource);
    return __.toNativeObject(bean.execute());
}

export interface GetPhrasesParams {
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
export function getPhrases(params: GetPhrasesParams): Phrases | null {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: GetDynamicPhrasesHandler = __.newBean<GetDynamicPhrasesHandler>('com.enonic.xp.lib.schema.GetDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListPhrasesParams {
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
export function listPhrases(params: ListPhrasesParams): Phrases[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicPhrasesHandler = __.newBean<ListDynamicPhrasesHandler>('com.enonic.xp.lib.schema.ListDynamicPhrasesHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeletePhrasesParams {
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
export function deletePhrases(params: DeletePhrasesParams): boolean {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicPhrasesHandler = __.newBean<DeleteDynamicPhrasesHandler>(
        'com.enonic.xp.lib.schema.DeleteDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
    return __.toNativeObject(bean.execute());
}

export interface ListComponentsParams {
    application: string;
}

interface ListDynamicComponentsHandler<T extends ComponentDescriptor> {
    setApplication(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): T[];
}

function doListComponents<T extends ComponentDescriptor>(params: ListComponentsParams, type: ComponentDescriptorType): T[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicComponentsHandler<T> = __.newBean<ListDynamicComponentsHandler<T>>(
        'com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(application);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
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
    return doListComponents(params, 'PART');
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
    return doListComponents(params, 'LAYOUT');
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
    return doListComponents(params, 'PAGE');
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

    return dispatchComponent<PartDescriptor[] | LayoutDescriptor[] | PageDescriptor[]>(type,
        () => listParts(params),
        () => listLayouts(params),
        () => listPages(params));
}

export interface ListSchemasParams {
    application: string;
}

interface ListDynamicSchemasHandler<T extends Schema> {
    setApplication(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): T[];
}

function doListContentSchemas<T extends Schema>(params: ListSchemasParams, type: ContentSchemaType): T[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicSchemasHandler<T> = __.newBean<ListDynamicSchemasHandler<T>>(
        'com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(application);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
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
    return doListContentSchemas(params, 'CONTENT_TYPE');
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
    return doListContentSchemas(params, 'FORM_FRAGMENT');
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
    return doListContentSchemas(params, 'MIXIN');
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

    return dispatchContentSchema<ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[]>(type,
        () => listContentTypes(params),
        () => listFormFragments(params),
        () => listMixins(params));
}

export interface MacroDescriptor {
    key: string;
    name: string;
    title: string;
    titleI18nKey: string;
    description: string;
    descriptionI18nKey: string;
    /**
     * @deprecated Not a dependable measure of when this last changed. Read from an application resource,
     * this derives from a jar entry timestamp that build tools normalize to a constant for reproducibility;
     * only a dynamic schema stored in a repository node carries a genuine time, and the two cannot be told
     * apart here.
     */
    modifiedTime: string;
    resource: string;
    form: FormItem[];
    config: Record<string, ConfigValue>;
    icon?: Icon;
}

export interface CreateOrUpdateMacroParams {
    key: string;
    resource?: string;
    icon?: IconInput | null;
}

interface CreateOrUpdateMacroHandler
    extends IconAwareHandler {
    setKey(value: string): void;

    setResource(value: string): void;

    execute(): MacroDescriptor;
}

/**
 * Creates the dynamic macro, or updates it when there is one, with its icon.
 *
 * `resource` is required to create the macro; on update it may be left out to change only the icon.
 * The icon is kept when `icon` is left out, removed when `null`, and replaced when `{data, mimeType}`
 * (`image/svg+xml` or `image/png`, at most 100 KB).
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro key.
 * @param {string} [params.resource] Macro resource value.
 * @param {object|null} [params.icon] Icon to store, or `null` to remove the stored icon.
 *
 * @returns {MacroDescriptor} the stored macro.
 */
export function createOrUpdateMacro(params: CreateOrUpdateMacroParams): MacroDescriptor {
    const key = checkRequired(params, 'key');

    const bean: CreateOrUpdateMacroHandler = __.newBean<CreateOrUpdateMacroHandler>('com.enonic.xp.lib.schema.CreateOrUpdateMacroHandler');
    bean.setKey(key);
    if (params.resource != null) {
        bean.setResource(params.resource);
    }
    applyIcon(bean, params.icon);
    return __.toNativeObject(bean.execute());
}

export interface GetMacroParams {
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
export function getMacro(params: GetMacroParams): MacroDescriptor | null {
    const key = checkRequired(params, 'key');

    const bean: GetDynamicMacroHandler = __.newBean<GetDynamicMacroHandler>('com.enonic.xp.lib.schema.GetDynamicMacroHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

export interface ListMacrosParams {
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
export function listMacros(params: ListMacrosParams): MacroDescriptor[] {
    const application = checkRequired(params, 'application');

    const bean: ListDynamicMacrosHandler = __.newBean<ListDynamicMacrosHandler>('com.enonic.xp.lib.schema.ListDynamicMacrosHandler');
    bean.setApplication(application);
    return __.toNativeObject(bean.execute());
}

export interface DeleteMacroParams {
    key: string;
}

interface DeleteDynamicMacroHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Removes dynamic macro resource, with its icon.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro resource descriptor key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMacro(params: DeleteMacroParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicMacroHandler = __.newBean<DeleteDynamicMacroHandler>('com.enonic.xp.lib.schema.DeleteDynamicMacroHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}