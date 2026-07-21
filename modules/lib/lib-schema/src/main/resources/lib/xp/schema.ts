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
 * Fetches application by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {Application | null} fetched application, or null if not found.
 */
export function get(params: GetApplicationParams): Application | null {
    const key = checkRequired(params, 'key');

    const bean: GetApplicationHandler = __.newBean<GetApplicationHandler>('com.enonic.xp.lib.schema.GetApplicationHandler');
    bean.setKey(key);
    return __.toNativeObject(bean.execute());
}

interface ListApplicationsHandler {
    execute(): Application[];
}

/**
 * Fetches both static and namespace (virtual) applications.
 *
 * @returns {Application[]} applications list.
 */
export function list(): Application[] {
    const bean: ListApplicationsHandler = __.newBean<ListApplicationsHandler>('com.enonic.xp.lib.schema.ListApplicationsHandler');
    return __.toNativeObject(bean.execute());
}

export interface Namespace {
    key: string;
    description?: string;
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
 * Creates a namespace (virtual application).
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

export interface DeleteNamespaceParams {
    key: string;
}

interface DeleteNamespaceHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Deletes a namespace (virtual application).
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
 * Fetches all available namespaces (virtual applications).
 *
 * @returns {Namespace[]} namespaces list.
 */
export function listNamespaces(): Namespace[] {
    const bean: ListNamespacesHandler = __.newBean<ListNamespacesHandler>('com.enonic.xp.lib.schema.ListNamespacesHandler');
    return __.toNativeObject(bean.execute());
}
