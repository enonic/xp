/**
 * Functions to access portal functionality.
 *
 * @example
 * var portalLib = require('/lib/xp/portal');
 *
 * @module portal
 */

declare global {
    interface XpLibraries {
        '/lib/xp/portal': typeof import('./portal');
    }
}

import type {ByteSource, Component, Content, Region, Request, ScriptValue} from '@enonic-types/core';

export type {
    Attachment,
    ByteSource,
    Content,
    Component,
    Region,
    Request,
    ScriptValue,
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

export type Site<Config> = Content<{
    description?: string;
    siteConfig: SiteConfig<Config> | SiteConfig<Config>[];
}, 'portal:site'>;

export interface SiteConfig<Config> {
    applicationKey: string;
    config: Config;
}

export type Without<T, U> = Partial<Record<Exclude<keyof T, keyof U>, never>>;
export type XOR<T, U> = T | U extends object ? (Without<T, U> & U) | (Without<U, T> & T) : T | U;

export type IdXorPath = XOR<{ id: string }, { path: string }>;

export interface AssetUrlParams {
    path: string;
    application?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface AssetUrlHandler {
    setPath(value: string): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    setApplication(value: string | null): void;

    createUrl(): string;
}

/**
 * @deprecated Use `libAsset` or `libStatic` instead. This function will be removed in future versions.
 *
 * This function generates a URL pointing to a static file.
 *
 * For backward compatibility, a `string` may be passed in place of `params`;
 * it is treated as `params.path` with all other fields defaulted.
 *
 * @example-ref examples/portal/assetUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.path Path to the asset.
 * @param {string} [params.application] Other application to reference to. Defaults to current application.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function assetUrl(params: AssetUrlParams | string): string {
    const normalized: AssetUrlParams = typeof params === 'string' ? {path: params} : params;

    const bean: AssetUrlHandler = __.newBean<AssetUrlHandler>('com.enonic.xp.lib.portal.url.AssetUrlHandler');

    const path = checkRequired(normalized, 'path');

    bean.setPath(path);
    bean.setUrlType(__.nullOrValue(normalized.type));
    bean.setApplication(__.nullOrValue(normalized.application));
    bean.setQueryParams(__.toScriptValue(normalized.params));

    return bean.createUrl();
}

export type ImageUrlParams = IdXorPath & {
    quality?: number;
    background?: string;
    format?: string;
    filter?: string;
    params?: object;
    type?: 'server' | 'absolute';
    scale:
        | `block(${number},${number})`
        | `height(${number})`
        | `max(${number})`
        | `square(${number})`
        | `wide(${number},${number})`
        | `width(${number})`
        | 'full';
    project?: string;
    branch?: string;
    baseUrl?: string;
};

interface ImageUrlHandler {
    setId(value: string | null): void;

    setPath(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    setProjectName(value: string | null): void;

    setBranch(value: string | null): void;

    setBaseUrl(value: string | null): void;

    setBackground(value: string | null): void;

    setQuality(value: number | null): void;

    setFilter(value: string | null): void;

    setFormat(value: string | null): void;

    setScale(value: string): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to an image.
 *
 * @example-ref examples/portal/imageUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] ID of the image content. Either `id` or `path` is required.
 * @param {string} [params.path] Path to the image. If `id` is specified, this parameter is not used.
 * @param {string} params.scale Required. Options are `width(px)`, `height(px)`, `block(width,height)`, `square(px)`, `max(px)`, `wide(width,height)` and `full`.
 * @param {number} [params.quality=85] Quality for JPEG images, ranges from 0 (max compression) to 100 (min compression).
 * @param {string} [params.background] Background color.
 * @param {string} [params.format] Format of the image.
 * @param {string} [params.filter] A number of filters are available to alter the image appearance, for example, blur(3), grayscale(), rounded(5), etc.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {string} [params.project] Name of the project.
 * @param {string} [params.branch] Name of the branch.
 * @param {string} [params.baseUrl] Custom baseUrl.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function imageUrl(params: ImageUrlParams): string {
    const bean: ImageUrlHandler = __.newBean<ImageUrlHandler>('com.enonic.xp.lib.portal.url.ImageUrlHandler');

    const scale = checkRequired(params, 'scale');

    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setQueryParams(__.toScriptValue(params.params));
    bean.setBackground(__.nullOrValue(params.background));
    bean.setQuality(__.nullOrValue(params.quality));
    bean.setFilter(__.nullOrValue(params.filter));
    bean.setFormat(__.nullOrValue(params.format));
    bean.setScale(scale);
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setBaseUrl(__.nullOrValue(params.baseUrl));

    return bean.createUrl();
}

export interface ComponentUrlParams {
    id?: string;
    path?: string;
    component?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface ComponentUrlHandler {
    setId(value: string | null): void;

    setPath(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    setComponent(value: string | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to a component.
 *
 * @example-ref examples/portal/componentUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] Id to the page.
 * @param {string} [params.path] Path to the page.
 * @param {string} [params.component] Path to the component. If not set, the current path is set.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function componentUrl(params: ComponentUrlParams): string {
    const bean: ComponentUrlHandler = __.newBean<ComponentUrlHandler>('com.enonic.xp.lib.portal.url.ComponentUrlHandler');

    bean.setId(__.nullOrValue(params?.id));
    bean.setPath(__.nullOrValue(params?.path));
    bean.setUrlType(__.nullOrValue(params?.type));
    bean.setComponent(__.nullOrValue(params?.component));
    bean.setQueryParams(__.toScriptValue(params?.params));

    return bean.createUrl();
}

export interface AttachmentUrlParams {
    id?: string;
    path?: string;
    name?: string;
    label?: string;
    download?: boolean;
    type?: 'server' | 'absolute';
    params?: object;
    project?: string;
    branch?: string;
    baseUrl?: string
}

interface AttachmentUrlHandler {
    setId(value: string | null): void;

    setPath(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    setName(value: string | null): void;

    setLabel(value: string | null): void;

    setProjectName(value: string | null): void;

    setBranch(value: string | null): void;

    setBaseUrl(value: string | null): void;

    setDownload(value: boolean): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to an attachment.
 *
 * @example-ref examples/portal/attachmentUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] Id to the content holding the attachment.
 * @param {string} [params.path] Path to the content holding the attachment.
 * @param {string} [params.name] Name of the attachment.
 * @param {string} [params.label=source] Label of the attachment.
 * @param {boolean} [params.download=false] Set to true if the disposition header should be set to attachment.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {string} [params.project] Name of the project.
 * @param {string} [params.branch] Name of the branch.
 * @param {string} [params.baseUrl] Custom baseUrl.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function attachmentUrl(params: AttachmentUrlParams): string {
    const bean: AttachmentUrlHandler = __.newBean<AttachmentUrlHandler>('com.enonic.xp.lib.portal.url.AttachmentUrlHandler');

    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setName(__.nullOrValue(params.name));
    bean.setLabel(__.nullOrValue(params.label));
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setDownload(params.download || false);
    bean.setQueryParams(__.toScriptValue(params.params));
    bean.setBaseUrl(__.nullOrValue(params.baseUrl));

    return bean.createUrl();
}

export type PageUrlParams = IdXorPath & {
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
    project?: string;
    branch?: string;
};

interface PageUrlHandler {
    setId(value: string | null): void;

    setPath(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    setProjectName(value: string | null): void;

    setBranch(value: string | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to a page.
 *
 * @example-ref examples/portal/pageUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] Id to the page. If id is set, then path is not used.
 * @param {string} [params.path] Path to the page. Relative paths is resolved using the context page.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {string} [params.project] Project of the context.
 * @param {string} [params.branch] Branch of the project for context.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function pageUrl(params: PageUrlParams): string {
    const bean: PageUrlHandler = __.newBean<PageUrlHandler>('com.enonic.xp.lib.portal.url.PageUrlHandler');

    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));
    bean.setUrlType(params.type || 'server');
    bean.setQueryParams(__.toScriptValue(params.params));
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));

    return bean.createUrl();
}

export interface ServiceUrlParams {
    service: string;
    application?: string;
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
}

interface ServiceUrlHandler {
    setService(value: string): void;

    setApplication(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to a service.
 *
 * @example-ref examples/portal/serviceUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.service Name of the service.
 * @param {string} [params.application] Other application to reference to. Default is current application.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function serviceUrl(params: ServiceUrlParams): string {
    const bean: ServiceUrlHandler = __.newBean<ServiceUrlHandler>('com.enonic.xp.lib.portal.url.ServiceUrlHandler');

    const service = checkRequired(params, 'service');

    bean.setService(service);
    bean.setApplication(__.nullOrValue(params.application));
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setQueryParams(__.toScriptValue(params.params));

    return bean.createUrl();
}

export interface IdProviderUrlParams {
    idProvider?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface IdProviderUrlHandler {
    setIdProvider(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to an ID provider.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.idProvider] Key of an ID provider.
 * If idProvider is not set, then the id provider corresponding to the current execution context will be used.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function idProviderUrl(params?: IdProviderUrlParams): string {
    const bean: IdProviderUrlHandler = __.newBean<IdProviderUrlHandler>('com.enonic.xp.lib.portal.url.IdProviderUrlHandler');

    bean.setIdProvider(__.nullOrValue(params?.idProvider));
    bean.setUrlType(__.nullOrValue(params?.type));
    bean.setQueryParams(__.toScriptValue(params?.params));

    return bean.createUrl();
}

export interface LoginUrlParams {
    idProvider?: string;
    redirect?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface LoginUrlHandler {
    setIdProvider(value: string | null): void;

    setUrlType(value: string | null): void;

    setRedirect(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to the login function of an ID provider.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.idProvider] Key of the id provider using an application.
 * If idProvider is not set, then the id provider corresponding to the current execution context will be used.
 * @param {string} [params.redirect] The URL to redirect to after the login.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function loginUrl(params?: LoginUrlParams): string {
    const bean: LoginUrlHandler = __.newBean<LoginUrlHandler>('com.enonic.xp.lib.portal.url.LoginUrlHandler');

    bean.setIdProvider(__.nullOrValue(params?.idProvider));
    bean.setRedirect(__.nullOrValue(params?.redirect));
    bean.setUrlType(__.nullOrValue(params?.type));
    bean.setQueryParams(__.toScriptValue(params?.params));

    return bean.createUrl();
}

export interface LogoutUrlParams {
    redirect?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface LogoutUrlHandler {
    setRedirect(value: string | null): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to the logout function of the application corresponding to the current user.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.redirect] The URL to redirect to after the logout.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function logoutUrl(params?: LogoutUrlParams): string {
    const bean: LogoutUrlHandler = __.newBean<LogoutUrlHandler>('com.enonic.xp.lib.portal.url.LogoutUrlHandler');

    bean.setRedirect(__.nullOrValue(params?.redirect));
    bean.setUrlType(__.nullOrValue(params?.type));
    bean.setQueryParams(__.toScriptValue(params?.params));

    return bean.createUrl();
}

export interface UrlParams {
    path: string | string[];
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
}

interface UrlHandler {
    setPath(value: string | ScriptValue): void;

    setUrlType(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL.
 *
 * @example-ref examples/portal/url.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string|string[]} params.path Path to the resource.
 *  If a string is provided, it is treated as a full path.
 *  If an array of strings is provided, each element is treated as a path segment and will be joined using '/'.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function url(params: UrlParams): string {
    const bean: UrlHandler = __.newBean<UrlHandler>('com.enonic.xp.lib.portal.url.UrlHandler');

    const path = checkRequired(params, 'path');

    if (Array.isArray(path)) {
        bean.setPath(__.toScriptValue(path));
    } else {
        bean.setPath(path);
    }
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setQueryParams(__.toScriptValue(params.params));

    return bean.createUrl();
}

export interface ProcessHtmlParams {
    value: string;
    type?: 'server' | 'absolute';
    imageWidths?: number[];
    imageSizes?: string;
}

interface ProcessHtmlHandler {
    setValue(value: string): void;

    setUrlType(value: string | null): void;

    setImageWidths(value: number[] | null): void;

    setImageSizes(value: string | null): void;

    createUrl(): string;
}

/**
 * This function replaces abstract internal links contained in an HTML text by generated URLs.
 *
 * When outputting processed HTML in Thymeleaf, use attribute `data-th-utext="${processedHtml}"`.
 *
 * @example-ref examples/portal/processHtml.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.value Html value string to process.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {number[]} [params.imageWidths] List of image width. Allows to generate image URLs for given image widths and use them in the `srcset` attribute of a `img` tag.
 * @param {string} [params.imageSizes] Specifies the width for an image depending on browser dimensions. The value has the following format: (media-condition) width. Multiple sizes are comma-separated.
 *
 * @returns {string} The processed HTML.
 */
export function processHtml(params: ProcessHtmlParams): string {
    const bean: ProcessHtmlHandler = __.newBean<ProcessHtmlHandler>('com.enonic.xp.lib.portal.url.ProcessHtmlHandler');

    const value = checkRequired(params, 'value');

    bean.setValue(value);
    bean.setUrlType(__.nullOrValue(params.type));
    bean.setImageWidths(__.nullOrValue(params.imageWidths));
    bean.setImageSizes(__.nullOrValue(params.imageSizes));

    return bean.createUrl();
}

interface SanitizeHtmlHandler {
    sanitizeHtml(value: string): string;
}

/**
 * This function sanitizes an HTML string by stripping all potentially unsafe tags and attributes.
 *
 * HTML sanitization can be used to protect against cross-site scripting (XSS) attacks by sanitizing any HTML code submitted by a user.
 *
 * @example-ref examples/portal/sanitizeHtml.js
 *
 * @param {string} html Html string value to process.
 *
 * @returns {string} The sanitized HTML.
 */
export function sanitizeHtml(html: string): string {
    const bean: SanitizeHtmlHandler = __.newBean<SanitizeHtmlHandler>('com.enonic.xp.lib.portal.SanitizeHtmlHandler');
    return __.toNativeObject(bean.sanitizeHtml(html));
}

interface GetCurrentSiteHandler {
    execute<Config>(): Site<Config> | null;
}

/**
 * This function returns the parent site of the content corresponding to the current execution context. It is meant to be
 * called from a page, layout or part controller.
 *
 * @example-ref examples/portal/getSite.js
 *
 * @returns {object|null} The current site as JSON.
 */
export function getSite<Config = Record<string, unknown>>(): Site<Config> | null {
    const bean: GetCurrentSiteHandler = __.newBean<GetCurrentSiteHandler>('com.enonic.xp.lib.portal.current.GetCurrentSiteHandler');
    return __.toNativeObject(bean.execute<Config>());
}

interface GetCurrentSiteConfigHandler {
    execute<Config>(): Config | null;
}

/**
 * This function returns the site configuration for this app in the parent site of the content corresponding to the current
 * execution context. It is meant to be called from a page, layout or part controller.
 *
 * @example-ref examples/portal/getSiteConfig.js
 *
 * @returns {object|null} The site configuration for current application as JSON.
 */
export function getSiteConfig<Config = Record<string, unknown>>(): Config | null {
    const bean: GetCurrentSiteConfigHandler = __.newBean<GetCurrentSiteConfigHandler>(
        'com.enonic.xp.lib.portal.current.GetCurrentSiteConfigHandler');
    return __.toNativeObject(bean.execute<Config>());
}

interface GetCurrentContentHandler {
    execute<Hit extends Content<unknown>>(): Hit | null;
}

/**
 * This function returns the content corresponding to the current execution context. It is meant to be called from a page, layout or
 * part controller
 *
 * @example-ref examples/portal/getContent.js
 *
 * @returns {object|null} The current content as JSON.
 */
export function getContent<Hit extends Content<unknown> = Content>(): Hit | null {
    const bean: GetCurrentContentHandler = __.newBean<GetCurrentContentHandler>(
        'com.enonic.xp.lib.portal.current.GetCurrentContentHandler');
    return __.toNativeObject(bean.execute<Hit>());
}

interface GetCurrentComponentHandler<_Component extends Component = Component> {
    execute(): _Component | null;
}

/**
 * This function returns the component corresponding to the current execution context. It is meant to be called
 * from a layout or part controller.
 *
 * @example-ref examples/portal/getComponent.js
 *
 * @returns {object|null} The current component as JSON.
 */
export function getComponent<
    _Component extends Component = Component
>(): _Component | null {
    const bean: GetCurrentComponentHandler<_Component> = __.newBean<GetCurrentComponentHandler<_Component>>(
        'com.enonic.xp.lib.portal.current.GetCurrentComponentHandler');
    return __.toNativeObject(bean.execute());
}

interface GetCurrentIdProviderKeyHandler {
    execute(): string | null;
}

/**
 * This function returns the id provider key corresponding to the current execution context.
 *
 * @example-ref examples/portal/getIdProviderKey.js
 *
 * @returns {string|null} The current id provider key, or `null` if no id provider is bound to the current context.
 */
export function getIdProviderKey(): string | null {
    const bean: GetCurrentIdProviderKeyHandler = __.newBean<GetCurrentIdProviderKeyHandler>(
        'com.enonic.xp.lib.portal.current.GetCurrentIdProviderKeyHandler');
    return __.toNativeObject(bean.execute());
}

export interface MultipartItem {
    name: string;
    fileName: string;
    contentType: string;
    size: number;
}

export type MultipartForm = Record<string, MultipartItem | MultipartItem[]>;

interface MultipartHandler {
    getForm(): MultipartForm;

    getItem(name: string, index: number): MultipartItem | null;

    getBytes(name: string, index: number): ByteSource | null;

    getText(name: string, index: number): string | null;
}

/**
 * This function returns a JSON containing multipart items. If the request is not multipart, an empty object is returned.
 *
 * @example-ref examples/portal/getMultipartForm.js
 *
 * @returns {object} The multipart form items.
 */
export function getMultipartForm(): MultipartForm {
    const bean: MultipartHandler = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return __.toNativeObject(bean.getForm());
}

/**
 * This function returns a JSON containing a named multipart item. If the item does not exist, it returns `null`.
 *
 * @example-ref examples/portal/getMultipartItem.js
 *
 * @param {string} name Name of the multipart item.
 * @param {number} [index] Optional zero-based index. It should be specified if there are multiple items with the same name.
 *
 * @returns {object|null} The named multipart form item.
 */
export function getMultipartItem(name: string, index = 0): MultipartItem | null {
    const bean: MultipartHandler = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return __.toNativeObject(bean.getItem(name, index));
}

/**
 * This function returns a data-stream for a named multipart item. If the item does not exist, it returns `null`.
 *
 * @example-ref examples/portal/getMultipartStream.js
 *
 * @param {string} name Name of the multipart item.
 * @param {number} [index] Optional zero-based index. It should be specified if there are multiple items with the same name.
 *
 * @returns {object|null} Stream of multipart item data.
 */
export function getMultipartStream(name: string, index = 0): ByteSource | null {
    const bean: MultipartHandler = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getBytes(name, index);
}

/**
 * This function returns the multipart item data as text.
 *
 * @example-ref examples/portal/getMultipartText.js
 *
 * @param {string} name Name of the multipart item.
 * @param {number} [index] Optional zero-based index. It should be specified if there are multiple items with the same name.
 *
 * @returns {string|null} Text for multipart item data.
 */
export function getMultipartText(name: string, index = 0): string | null {
    const bean: MultipartHandler = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getText(name, index);
}

export interface ImagePlaceholderParams {
    width?: number;
    height?: number;
}

interface ImagePlaceholderHandler {
    setWidth(value: number): void;

    setHeight(value: number): void;

    createImagePlaceholder(): string;
}

/**
 * This function generates a URL to an image placeholder.
 *
 * @example-ref examples/portal/imagePlaceholder.js
 *
 * @param {object} params Image parameters as JSON.
 * @param {number} params.width Width of the image in pixels.
 * @param {number} params.height Height of the image in pixels.
 *
 * @returns {string} Placeholder image URL.
 */
export function imagePlaceholder(params: ImagePlaceholderParams): string {
    const bean: ImagePlaceholderHandler = __.newBean<ImagePlaceholderHandler>('com.enonic.xp.lib.portal.url.ImagePlaceholderHandler');
    bean.setWidth(params?.width ?? 0);
    bean.setHeight(params?.height ?? 0);
    return bean.createImagePlaceholder();
}

export interface ApiUrlParams {
    api: string;
    type?: 'server' | 'absolute' | 'websocket';
    params?: Record<string, string | string[]>;
    path?: string | string[];
    baseUrl?: string;
}

interface ApiUrlHandler {
    setApi(value: string): void;

    setUrlType(value: string | null): void;

    setPath(value: ScriptValue | null): void;

    setBaseUrl(value: string | null): void;

    setQueryParams(value: ScriptValue | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to a Universal API.
 *
 * @example-ref examples/portal/apiUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.api Descriptor of the API
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {string|string[]} [params.path] Path(s) to be appended to the base URL following the api segment to complete request URL.
 * @param {object} [params.params] Custom query parameters to append to the URL.
 * @param {string} [params.baseUrl] Custom baseUrl.
 *
 * @returns {string} The generated URL.
 */
export function apiUrl(params: ApiUrlParams): string {
    const api = checkRequired(params, 'api');

    const {
        type,
        path,
        params: queryParams,
        baseUrl,
    } = params ?? {};

    const bean: ApiUrlHandler = __.newBean<ApiUrlHandler>('com.enonic.xp.lib.portal.url.ApiUrlHandler');

    bean.setApi(api);
    bean.setUrlType(__.nullOrValue(type));
    bean.setQueryParams(__.toScriptValue(queryParams));
    bean.setBaseUrl(__.nullOrValue(baseUrl));
    bean.setPath(__.toScriptValue(path));

    return bean.createUrl();
}

export interface BaseUrlParams {
    type?: 'server' | 'absolute' | 'websocket';
    id?: string;
    path?: string;
    project?: string;
    branch?: string;
}

interface BaseUrlHandler {
    setUrlType(value: string | null): void;

    setProjectName(value: string | null): void;

    setBranch(value: string | null): void;

    setId(value: string | null): void;

    setPath(value: string | null): void;

    createUrl(): string;
}

/**
 * This function generates a baseURL.
 *
 * @example-ref examples/portal/baseUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {string} [params.id] ID of the content.
 * @param {string} [params.path] Path to the content.
 * @param {string} [params.project] Name of the project to use for resolving the URL.
 * @param {string} [params.branch] Name of the branch to use for resolving the URL.
 *
 * @returns {string} The generated URL.
 */
export function baseUrl(params: BaseUrlParams): string {
    const bean: BaseUrlHandler = __.newBean<BaseUrlHandler>('com.enonic.xp.lib.portal.url.BaseUrlHandler');

    bean.setUrlType(__.nullOrValue(params.type));
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));

    return bean.createUrl();
}

export interface MacroContext {
    body: string;
    params: Record<string, string | undefined>;
    request: Request;
}

export type CspHashAlgo = 'sha256' | 'sha384' | 'sha512';

/**
 * A hash source for `script-src` / `style-src`. Either inline `content` to be digested
 * (`algo` defaults to `'sha256'`), or a precomputed base64 `hash` with its `algo` — the two are
 * mutually exclusive.
 */
export type CspHashSource = XOR<{ content: string; algo?: CspHashAlgo }, { hash: string; algo: CspHashAlgo }>;

/**
 * Common source-list values for a CSP source expression, per W3C CSP3. Keyword sources are
 * single-quoted (e.g. `'self'`); scheme sources are verbatim (e.g. `data:`). Variadic source
 * params on {@link Csp} accept these values or raw strings (hosts, other schemes, paths, URLs).
 */
export const CspSource = {
    SELF: "'self'",
    NONE: "'none'",
    UNSAFE_INLINE: "'unsafe-inline'",
    UNSAFE_EVAL: "'unsafe-eval'",
    STRICT_DYNAMIC: "'strict-dynamic'",
    UNSAFE_HASHES: "'unsafe-hashes'",
    WASM_UNSAFE_EVAL: "'wasm-unsafe-eval'",
    REPORT_SAMPLE: "'report-sample'",
    DATA: 'data:',
    BLOB: 'blob:',
    WILDCARD: '*',
} as const;

export type CspSource = typeof CspSource[keyof typeof CspSource];

/**
 * Flags allowed in a CSP `sandbox` directive. Sandbox tokens are emitted unquoted,
 * as the CSP spec defines them.
 */
export const SandboxFlag = {
    ALLOW_SCRIPTS: 'allow-scripts',
    ALLOW_SAME_ORIGIN: 'allow-same-origin',
    ALLOW_FORMS: 'allow-forms',
    ALLOW_POPUPS: 'allow-popups',
    ALLOW_MODALS: 'allow-modals',
    ALLOW_TOP_NAVIGATION: 'allow-top-navigation',
    ALLOW_DOWNLOADS: 'allow-downloads',
    ALLOW_POINTER_LOCK: 'allow-pointer-lock',
    ALLOW_PRESENTATION: 'allow-presentation',
    ALLOW_ORIENTATION_LOCK: 'allow-orientation-lock',
} as const;

export type SandboxFlag = typeof SandboxFlag[keyof typeof SandboxFlag];

/**
 * Special keywords for the `trusted-types` directive, used alongside (user-defined) policy names.
 * `'none'` / `'allow-duplicates'` are single-quoted; `WILDCARD` is the bare `*`.
 */
export const TrustedTypesKeyword = {
    ALLOW_DUPLICATES: "'allow-duplicates'",
    NONE: "'none'",
    WILDCARD: '*',
} as const;

export type TrustedTypesKeyword = typeof TrustedTypesKeyword[keyof typeof TrustedTypesKeyword];

/**
 * A request-scoped Content Security Policy builder. The same instance is returned for the lifetime
 * of the current portal request, so controllers, layouts, parts and widgets can each contribute to
 * the final policy. The header is emitted as `Content-Security-Policy` at response-flush time, so
 * late additions during rendering still land.
 *
 * Contributions are merged by plain **union**: each directive's source list is the union of every
 * contributor's sources, emitted as written.
 * - {@link add} unions sources into a directive (deduped).
 * - {@link override} replaces a directive's sources — no freeze, so a later {@link add} still extends.
 *
 * The builder deliberately does **not** arbitrate between sources that interact in the browser —
 * notably `'unsafe-inline'` sharing a directive with a `'nonce-…'`, hash, or `'strict-dynamic'`, which
 * makes the browser ignore `'unsafe-inline'`. That precedence is the browser's documented behaviour and
 * the secure default; the API emits what you declare. A contributor that genuinely needs the looser
 * source to win — e.g. an editor that must allow inline styles over a strict `style-src` — uses
 * {@link override} to replace the directive, which drops the nonce/hash that would otherwise neutralize
 * `'unsafe-inline'`.
 */
export interface Csp {
    /**
     * Seeds a restrictive deny-all baseline (`default-src 'none'`, `base-uri 'none'`,
     * `frame-ancestors 'none'`). Call it first, then open up only the directives you need.
     */
    strict(): Csp;

    /** Unions source expressions into `default-src`, the fetch directive the other fetch directives fall back to when omitted. */
    defaultSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `script-src` (fetch directive for JavaScript and WebAssembly); the fallback for `script-src-elem` and `script-src-attr`. */
    scriptSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `style-src` (fetch directive for stylesheets); the fallback for `style-src-elem` and `style-src-attr`. */
    styleSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `img-src` (fetch directive for images and favicons). */
    imgSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `font-src` (fetch directive for fonts loaded with `@font-face`). */
    fontSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `connect-src` (fetch directive for script interfaces: `fetch`, `XMLHttpRequest`, `WebSocket`, `EventSource`, `<a ping>`). */
    connectSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `media-src` (fetch directive for `<audio>`, `<video>`, `<track>`). */
    mediaSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `object-src` (fetch directive for `<object>` and `<embed>`). */
    objectSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `frame-src` (fetch directive for nested browsing contexts: `<frame>`, `<iframe>`); falls back to `child-src`. */
    frameSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `worker-src` (fetch directive for `Worker`, `SharedWorker`, `ServiceWorker`); falls back to `child-src`. */
    workerSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `manifest-src` (fetch directive for application manifest files). */
    manifestSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `child-src` (fetch directive; the fallback for `frame-src` and `worker-src`). */
    childSrc(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `frame-ancestors` (navigation directive: which parents may embed this page via `<frame>`/`<iframe>`/`<object>`/`<embed>`). Does not fall back to `default-src`. */
    frameAncestors(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `base-uri` (document directive restricting the URLs allowed in a `<base>` element). Does not fall back to `default-src`. */
    baseUri(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `form-action` (navigation directive restricting the targets of form submissions). */
    formAction(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `script-src-elem` (fetch directive for `<script>` elements); falls back to `script-src`. */
    scriptSrcElem(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `script-src-attr` (fetch directive for inline event handlers, e.g. `onclick`); falls back to `script-src`. */
    scriptSrcAttr(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `style-src-elem` (fetch directive for `<style>` and `<link rel="stylesheet">`); falls back to `style-src`. */
    styleSrcElem(...sources: (CspSource | string)[]): Csp;

    /** Unions source expressions into `style-src-attr` (fetch directive for inline `style` attributes); falls back to `style-src`. */
    styleSrcAttr(...sources: (CspSource | string)[]): Csp;

    /**
     * Adds the `report-to` reporting directive naming a reporting endpoint group, which must be defined
     * by a companion `Reporting-Endpoints` response header (the caller's responsibility). Violation
     * reports are then POSTed to that endpoint. The older `report-uri` directive is deprecated — use
     * `add('report-uri', ...)` if still needed.
     */
    reportTo(group: string): Csp;

    /** Registers `require-trusted-types-for 'script'`, enforcing Trusted Types at injection sinks. */
    requireTrustedTypesForScript(): Csp;

    /** Adds policy names and/or {@link TrustedTypesKeyword} keywords to the `trusted-types` directive. */
    trustedTypes(...values: (TrustedTypesKeyword | string)[]): Csp;

    /**
     * Unions sandboxing flags into the `sandbox` directive — a document directive that applies an HTML
     * sandbox to the resource. With no flags, registers a bare `sandbox` (all restrictions applied).
     */
    sandbox(...flags: SandboxFlag[]): Csp;

    /**
     * Unions a hash-source (`'<algo>-<base64>'`) into `script-src`. Pass `{ content }` to digest inline
     * script text (`algo` defaults to `'sha256'`), or `{ hash, algo }` for a precomputed base64 digest.
     */
    shaScriptSrc(source: CspHashSource): Csp;

    /**
     * Unions a hash-source (`'<algo>-<base64>'`) into `style-src`. Pass `{ content }` to digest inline
     * style text (`algo` defaults to `'sha256'`), or `{ hash, algo }` for a precomputed base64 digest.
     */
    shaStyleSrc(source: CspHashSource): Csp;

    /**
     * Adds a nonce-source (`'nonce-<base64>'`) carrying the per-request nonce to `script-src` and
     * returns the base64 value — set it as the `nonce` attribute of the matching inline `<script>`.
     * Always the same value within a request.
     */
    nonceScriptSrc(): string;

    /**
     * Adds a nonce-source for the per-request nonce to `script-src-elem` and returns its base64 value —
     * for a `<script>` element that must satisfy a page whose `script-src-elem` uses `'strict-dynamic'`
     * (under which `'self'` and host-sources are ignored). Always the same value within a request.
     */
    nonceScriptSrcElem(): string;

    /**
     * Adds a nonce-source (`'nonce-<base64>'`) carrying the per-request nonce to `style-src` and returns
     * the base64 value — set it as the `nonce` attribute of the matching inline `<style>`. Always the
     * same value within a request.
     */
    nonceStyleSrc(): string;

    /**
     * Adds a nonce-source for the per-request nonce to `style-src-elem` and returns its base64 value —
     * for a `<style>` element that must satisfy a page whose `style-src-elem` uses `'strict-dynamic'`.
     * Always the same value within a request.
     */
    nonceStyleSrcElem(): string;

    // Low-level, string-based building blocks — prefer the typed methods above; reach for these for
    // directives the typed API does not cover, to read/inspect, or for whole-policy operations.

    /**
     * Unions source expressions into the serialized-source-list for `directive`, de-duplicated. With
     * no source expressions, registers a valueless directive (e.g. `upgrade-insecure-requests`).
     *
     * A nonce-source is rejected: only the `nonce*` methods mint the per-request nonce.
     */
    add(directive: string, ...sources: string[]): Csp;

    /**
     * Parses a serialized policy (a `Content-Security-Policy` header value) and unions each directive's
     * source expressions onto this policy — the additive counterpart to {@link resetTo}. Existing
     * directives are extended and absent ones added, so you can grant extra permissions on top of a
     * policy built elsewhere without restating it (a nonce-source already wired into
     * `script-src`/`style-src` is kept). Lenient like {@link resetTo}: invalid tokens are skipped and
     * nonce-sources dropped. `null`/`undefined` adds nothing. A comma-separated list of policies is
     * flattened into one additive directive set (no extra enforced policy is created). Additive.
     */
    merge(headerValue?: string | null): Csp;

    /**
     * The source expressions currently declared for `directive`, in serialized order (already
     * de-duplicated), or `null` if no contributor has declared it. A declared valueless directive
     * (e.g. `upgrade-insecure-requests`) returns an empty array. Lets a contributor inspect before it
     * `override`s or gap-fills (`if (csp.directive(name) === null) csp.add(name, ...)`). Reads this
     * policy only; the report-only policy is reached via {@link cspReportOnly}.
     */
    directive(directive: string): string[] | null;

    /**
     * Replaces `directive`'s serialized-source-list with exactly these source expressions, overriding
     * what other contributors set. Policy-level and **not** additive — it can narrow the policy. No
     * freeze, so a later {@link add} still extends. To remove a directive entirely, use {@link reset}.
     */
    override(directive: string, ...sources: string[]): Csp;

    /**
     * Removes the named directives, overriding what other contributors set — e.g.
     * `reset('upgrade-insecure-requests')` is how a boolean directive is unset. With no argument,
     * removes nothing; to clear the whole policy use {@link resetTo} with an empty value.
     * Policy-level and **not** additive.
     */
    reset(...directives: string[]): Csp;

    /**
     * Replaces this policy's directives with the single policy parsed from a raw header value, so later
     * contributions still apply on top. A `null`, `undefined`, empty or blank value clears the
     * directives — if nothing is added afterwards, no header is emitted. The request nonce stays
     * stable. Parsing is lenient, mirroring the browser: invalid tokens are skipped, and of repeated
     * directives only the first occurrence counts. `'nonce-…'` sources are dropped — use the `nonce*`
     * methods. A `,` (which would begin a further policy) and everything after it is ignored — only the
     * first policy is applied. Policy-level and **not** additive.
     */
    resetTo(headerValue?: string | null): Csp;
}

interface CspHandler {
    setReportOnly(reportOnly: boolean): void;

    add(directive: string, sources: string[]): void;

    merge(headerValue: string | null): void;

    directive(directive: string): string[] | null;

    override(directive: string, sources: string[]): void;

    reset(directives: string[]): void;

    resetTo(headerValue: string | null): void;

    strict(): void;

    defaultSrc(sources: string[]): void;

    scriptSrc(sources: string[]): void;

    styleSrc(sources: string[]): void;

    imgSrc(sources: string[]): void;

    fontSrc(sources: string[]): void;

    connectSrc(sources: string[]): void;

    mediaSrc(sources: string[]): void;

    objectSrc(sources: string[]): void;

    frameSrc(sources: string[]): void;

    workerSrc(sources: string[]): void;

    manifestSrc(sources: string[]): void;

    childSrc(sources: string[]): void;

    frameAncestors(sources: string[]): void;

    baseUri(sources: string[]): void;

    formAction(sources: string[]): void;

    scriptSrcElem(sources: string[]): void;

    scriptSrcAttr(sources: string[]): void;

    styleSrcElem(sources: string[]): void;

    styleSrcAttr(sources: string[]): void;

    reportTo(group: string): void;

    requireTrustedTypesForScript(): void;

    trustedTypes(values: string[]): void;

    sandbox(flags: string[]): void;

    shaScriptSrcContent(content: string, algo: string | null): void;

    shaScriptSrcDigest(base64: string, algo: string): void;

    shaStyleSrcContent(content: string, algo: string | null): void;

    shaStyleSrcDigest(base64: string, algo: string): void;

    nonceScriptSrc(): string;

    nonceScriptSrcElem(): string;

    nonceStyleSrc(): string;

    nonceStyleSrcElem(): string;
}

/**
 * Returns a handle to the request-scoped enforced Content Security Policy (emitted as the
 * `Content-Security-Policy` header). Each call returns a new handle, but all handles are backed by
 * the same policy bound to the current portal request — contributions through any of them land in
 * the same emitted header. For the report-only companion, use {@link cspReportOnly}.
 *
 * @example-ref examples/portal/csp.js
 *
 * @returns {Csp} The enforced Content Security Policy bound to the current portal request.
 */
export function csp(): Csp {
    return createCsp(false);
}

/**
 * Returns a handle to the request-scoped report-only Content Security Policy (emitted as the
 * `Content-Security-Policy-Report-Only` header). It is an independent companion to the enforced
 * policy from {@link csp} — same API, shares the request nonce — and the two headers can coexist on
 * one response (e.g. enforce a settled policy while trialling a stricter one). While left empty, no
 * report-only header is emitted.
 *
 * @returns {Csp} The report-only Content Security Policy bound to the current portal request.
 */
export function cspReportOnly(): Csp {
    return createCsp(true);
}

function createCsp(reportOnly: boolean): Csp {
    const bean: CspHandler = __.newBean<CspHandler>('com.enonic.xp.lib.portal.csp.CspHandler');
    bean.setReportOnly(reportOnly);

    const instance: Csp = {
        add(directive: string, ...sources: string[]): Csp {
            bean.add(directive, sources);
            return instance;
        },
        merge(headerValue?: string | null): Csp {
            bean.merge(headerValue ?? null);
            return instance;
        },
        directive(directive: string): string[] | null {
            return __.toNativeObject(bean.directive(directive));
        },
        override(directive: string, ...sources: string[]): Csp {
            bean.override(directive, sources);
            return instance;
        },
        reset(...directives: string[]): Csp {
            bean.reset(directives);
            return instance;
        },
        resetTo(headerValue?: string | null): Csp {
            bean.resetTo(headerValue ?? null);
            return instance;
        },
        strict(): Csp {
            bean.strict();
            return instance;
        },
        defaultSrc(...sources: (CspSource | string)[]): Csp {
            bean.defaultSrc(sources);
            return instance;
        },
        scriptSrc(...sources: (CspSource | string)[]): Csp {
            bean.scriptSrc(sources);
            return instance;
        },
        styleSrc(...sources: (CspSource | string)[]): Csp {
            bean.styleSrc(sources);
            return instance;
        },
        imgSrc(...sources: (CspSource | string)[]): Csp {
            bean.imgSrc(sources);
            return instance;
        },
        fontSrc(...sources: (CspSource | string)[]): Csp {
            bean.fontSrc(sources);
            return instance;
        },
        connectSrc(...sources: (CspSource | string)[]): Csp {
            bean.connectSrc(sources);
            return instance;
        },
        mediaSrc(...sources: (CspSource | string)[]): Csp {
            bean.mediaSrc(sources);
            return instance;
        },
        objectSrc(...sources: (CspSource | string)[]): Csp {
            bean.objectSrc(sources);
            return instance;
        },
        frameSrc(...sources: (CspSource | string)[]): Csp {
            bean.frameSrc(sources);
            return instance;
        },
        workerSrc(...sources: (CspSource | string)[]): Csp {
            bean.workerSrc(sources);
            return instance;
        },
        manifestSrc(...sources: (CspSource | string)[]): Csp {
            bean.manifestSrc(sources);
            return instance;
        },
        childSrc(...sources: (CspSource | string)[]): Csp {
            bean.childSrc(sources);
            return instance;
        },
        frameAncestors(...sources: (CspSource | string)[]): Csp {
            bean.frameAncestors(sources);
            return instance;
        },
        baseUri(...sources: (CspSource | string)[]): Csp {
            bean.baseUri(sources);
            return instance;
        },
        formAction(...sources: (CspSource | string)[]): Csp {
            bean.formAction(sources);
            return instance;
        },
        scriptSrcElem(...sources: (CspSource | string)[]): Csp {
            bean.scriptSrcElem(sources);
            return instance;
        },
        scriptSrcAttr(...sources: (CspSource | string)[]): Csp {
            bean.scriptSrcAttr(sources);
            return instance;
        },
        styleSrcElem(...sources: (CspSource | string)[]): Csp {
            bean.styleSrcElem(sources);
            return instance;
        },
        styleSrcAttr(...sources: (CspSource | string)[]): Csp {
            bean.styleSrcAttr(sources);
            return instance;
        },
        reportTo(group: string): Csp {
            bean.reportTo(group);
            return instance;
        },
        requireTrustedTypesForScript(): Csp {
            bean.requireTrustedTypesForScript();
            return instance;
        },
        trustedTypes(...values: (TrustedTypesKeyword | string)[]): Csp {
            bean.trustedTypes(values);
            return instance;
        },
        sandbox(...flags: SandboxFlag[]): Csp {
            bean.sandbox(flags);
            return instance;
        },
        shaScriptSrc(source: CspHashSource): Csp {
            if (source.content !== undefined) {
                bean.shaScriptSrcContent(source.content, __.nullOrValue(source.algo));
            } else {
                bean.shaScriptSrcDigest(source.hash, source.algo);
            }
            return instance;
        },
        shaStyleSrc(source: CspHashSource): Csp {
            if (source.content !== undefined) {
                bean.shaStyleSrcContent(source.content, __.nullOrValue(source.algo));
            } else {
                bean.shaStyleSrcDigest(source.hash, source.algo);
            }
            return instance;
        },
        nonceScriptSrc(): string {
            return bean.nonceScriptSrc();
        },
        nonceScriptSrcElem(): string {
            return bean.nonceScriptSrcElem();
        },
        nonceStyleSrc(): string {
            return bean.nonceStyleSrc();
        },
        nonceStyleSrcElem(): string {
            return bean.nonceStyleSrcElem();
        },
    };
    return instance;
}
