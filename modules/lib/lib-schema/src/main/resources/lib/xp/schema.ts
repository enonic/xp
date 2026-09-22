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

export interface CreateDynamicContentSchemaParams {
    name: string;
    type: ContentSchemaType;
    resource: string;
}

interface CreateDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    setResource(value: string): void;

    execute(): ContentTypeSchema | FormFragmentSchema | MixinSchema;
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
 * Creates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Schema resource name.
 * @param {string} params.type Schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} created resource.
 */
export function createSchema(params: CreateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    const name = checkRequired(params, 'name');
    const type = checkRequired(params, 'type');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicContentSchemaHandler = __.newBean<CreateDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    bean.setResource(resource);
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

    setResource(value: string): void;

    execute(): LayoutDescriptor | PageDescriptor | PartDescriptor;
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
 * Creates dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} created resource.
 */
export function createComponent(params: CreateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const key = checkRequired(params, 'key');
    const type = checkRequired(params, 'type');
    const resource = checkRequired(params, 'resource');

    const bean: CreateDynamicComponentHandler = __.newBean<CreateDynamicComponentHandler>('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');

    bean.setKey(key);
    bean.setType(type);
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
    type: ContentSchemaType;
}

interface GetDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): ContentTypeSchema | FormFragmentSchema | MixinSchema | null;
}

/**
 * Fetches dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema | null} fetched resource, or `null` if not found.
 */
export function getSchema(params: GetDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema | null {
    const name = checkRequired(params, 'name');
    const type = checkRequired(params, 'type');

    const bean: GetDynamicContentSchemaHandler = __.newBean<GetDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

export interface GetDynamicComponentParams {
    key: string;
    type: ComponentDescriptorType;
}

interface GetDynamicComponentHandler {
    setKey(value: string): void;

    setType(value: ComponentDescriptorType): void;

    execute(): LayoutDescriptor | PageDescriptor | PartDescriptor;
}

/**
 * Fetches dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} fetched resource.
 */
export function getComponent(params: GetDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const key = checkRequired(params, 'key');
    const type = checkRequired(params, 'type');

    const bean: GetDynamicComponentHandler = __.newBean<GetDynamicComponentHandler>('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(key);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

export interface SiteDescriptor {
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
 * @returns {SiteDescriptor | null} fetched resource, or `null` if not found.
 */
export function getSite(params: GetDynamicSiteParams): SiteDescriptor | null {
    const application = checkRequired(params, 'application');

    const bean: GetDynamicSiteHandler = __.newBean<GetDynamicSiteHandler>('com.enonic.xp.lib.schema.GetDynamicSiteHandler');
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
    type: ContentSchemaType;
}

interface DeleteDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

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
    const name = checkRequired(params, 'name');
    const type = checkRequired(params, 'type');

    const bean: DeleteDynamicContentSchemaHandler = __.newBean<DeleteDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
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
    const key = checkRequired(params, 'key');
    const type = checkRequired(params, 'type');

    const bean: DeleteDynamicComponentHandler = __.newBean<DeleteDynamicComponentHandler>('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(key);
    bean.setType(type);
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
    type: ContentSchemaType;
    resource: string;
}

interface UpdateDynamicContentSchemaHandler {
    setName(value: string): void;

    setType(value: ContentSchemaType): void;

    setResource(value: string): void;

    execute(): ContentTypeSchema | FormFragmentSchema | MixinSchema;
}

/**
 * Updates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 * @param {string} params.resource Schema resource value.
 *
 * @returns {ContentTypeSchema | FormFragmentSchema | MixinSchema} updated resource.
 */
export function updateSchema(params: UpdateDynamicContentSchemaParams): ContentTypeSchema | FormFragmentSchema | MixinSchema {
    const name = checkRequired(params, 'name');
    const type = checkRequired(params, 'type');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicContentSchemaHandler = __.newBean<UpdateDynamicContentSchemaHandler>('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(name);
    bean.setType(type);
    bean.setResource(resource);
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
 * @param {string} params.resource Component resource value.
 *
 * @returns {LayoutDescriptor | PageDescriptor | PartDescriptor} updated resource.
 */
export function updateComponent(params: UpdateDynamicComponentParams): LayoutDescriptor | PageDescriptor | PartDescriptor {
    const key = checkRequired(params, 'key');
    const type = checkRequired(params, 'type');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicComponentHandler = __.newBean<UpdateDynamicComponentHandler>('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(key);
    bean.setType(type);
    bean.setResource(resource);

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
 * @param {string} params.resource Site schema resource value.
 *
 * @returns {SiteDescriptor} updated resource.
 */
export function updateSite(params: UpdateDynamicSiteParams): SiteDescriptor {
    const application = checkRequired(params, 'application');
    const resource = checkRequired(params, 'resource');

    const bean: UpdateDynamicSiteHandler = __.newBean<UpdateDynamicSiteHandler>('com.enonic.xp.lib.schema.UpdateDynamicSiteHandler');
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
    const application = checkRequired(params, 'application');
    const type = checkRequired(params, 'type');

    const bean: ListDynamicComponentsHandler = __.newBean<ListDynamicComponentsHandler>('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setApplication(application);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

export interface ListDynamicSchemasParams {
    application: string;
    type: ContentSchemaType;
}

interface ListDynamicSchemasHandler {
    setApplication(value: string): void;

    setType(value: ContentSchemaType): void;

    execute(): ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[];
}

/**
 * Fetches dynamic content schemas resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.application Application key.
 * @param {string} params.type Content schema type.
 *
 * @returns {ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[]} fetched resources.
 */
export function listSchemas(params: ListDynamicSchemasParams): ContentTypeSchema[] | FormFragmentSchema[] | MixinSchema[] {
    const application = checkRequired(params, 'application');
    const type = checkRequired(params, 'type');

    const bean: ListDynamicSchemasHandler = __.newBean<ListDynamicSchemasHandler>('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setApplication(application);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
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
 * @param {string} params.key Macro descriptor key, e.g. `myapp:mymacro`.
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
 * @param {string} params.key Macro descriptor key, e.g. `myapp:mymacro`.
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
 * @param {string} params.key Macro descriptor key, e.g. `myapp:mymacro`.
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
 * Fetches dynamic macro resources of an application.
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
 * Removes dynamic macro resource, its icon included.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Macro descriptor key, e.g. `myapp:mymacro`.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deleteMacro(params: DeleteDynamicMacroParams): boolean {
    const key = checkRequired(params, 'key');

    const bean: DeleteDynamicMacroHandler = __.newBean<DeleteDynamicMacroHandler>('com.enonic.xp.lib.schema.DeleteDynamicMacroHandler');
    bean.setKey(key);
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
 * Creates dynamic i18n phrases resource (`cms/i18n/phrases/<name>.properties`).
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
 * @returns {boolean} true if succeeded, false otherwise.
 */
export function deletePhrases(params: DeleteDynamicPhrasesParams): boolean {
    const application = checkRequired(params, 'application');
    const name = checkRequired(params, 'name');

    const bean: DeleteDynamicPhrasesHandler = __.newBean<DeleteDynamicPhrasesHandler>('com.enonic.xp.lib.schema.DeleteDynamicPhrasesHandler');
    bean.setApplication(application);
    bean.setName(name);
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

interface SetDynamicIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    setData(value: ByteSource): void;

    setMimeType(value: string): void;

    execute(): Icon;
}

interface GetDynamicIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    execute(): Icon | null;
}

interface DeleteDynamicIconHandler {
    setName(value: string): void;

    setType(value: IconType): void;

    execute(): boolean;
}

function doSetIcon(name: string, type: IconType, data: ByteSource, mimeType: string): Icon {
    const bean: SetDynamicIconHandler = __.newBean<SetDynamicIconHandler>('com.enonic.xp.lib.schema.SetDynamicIconHandler');
    bean.setName(name);
    bean.setType(type);
    bean.setData(data);
    bean.setMimeType(mimeType);
    return __.toNativeObject(bean.execute());
}

function doGetIcon(name: string, type: IconType): Icon | null {
    const bean: GetDynamicIconHandler = __.newBean<GetDynamicIconHandler>('com.enonic.xp.lib.schema.GetDynamicIconHandler');
    bean.setName(name);
    bean.setType(type);
    return __.toNativeObject(bean.execute());
}

function doDeleteIcon(name: string, type: IconType): boolean {
    const bean: DeleteDynamicIconHandler = __.newBean<DeleteDynamicIconHandler>('com.enonic.xp.lib.schema.DeleteDynamicIconHandler');
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
