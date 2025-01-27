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

import type {ByteSource, Component, Content, Region, ScriptValue,} from '@enonic-types/core';

export type {
    Attachment,
    ByteSource,
    Content,
    Component,
    Region,
} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export type Site<Config> = Content<{
    description?: string;
    siteConfig: SiteConfig<Config> | SiteConfig<Config>[];
}, 'portal:site'>;

export interface SiteConfig<Config> {
    applicationKey: string;
    config: Config;
}

export type Without<T, U> = { [P in Exclude<keyof T, keyof U>]?: never };
export type XOR<T, U> = T | U extends object ? (Without<T, U> & U) | (Without<U, T> & T) : T | U;

export type IdXorPath = XOR<{ id: string }, { path: string }>;

export interface AssetUrlParams {
    path: string;
    application?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface AssetUrlHandler {
    createUrl(value: object): string;
}

/**
 * @deprecated Use `libAsset` or `libStatic` instead. This function will be removed in future versions.
 *
 * This function generates a URL pointing to a static file.
 *
 * @example-ref examples/portal/assetUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.path Path to the asset.
 * @param {string} [params.application] Other application to reference to. Defaults to current application.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function assetUrl(params: AssetUrlParams): string {
    const bean: AssetUrlHandler = __.newBean<AssetUrlHandler>('com.enonic.xp.lib.portal.url.AssetUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export type ImageUrlParams = IdXorPath & {
    quality?: number;
    background?: string;
    format?: string;
    filter?: string;
    server?: string;
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
    baseUrlKey?: string;
    offline?: boolean | undefined;
};

interface ImageUrlHandler {
    setId(value?: string | null): void;

    setPath(value?: string | null): void;

    setUrlType(value?: string | null): void;

    setQueryParams(value?: ScriptValue | null): void;

    setProjectName(value?: string | null): void;

    setBranch(value?: string | null): void;

    setBackground(value?: string | null): void;

    setQuality(value?: number | null): void;

    setFilter(value?: string | null): void;

    setFormat(value?: string | null): void;

    setScale(value: string): void;

    setOffline(value: boolean): void;

    setBaseUrlKey(value?: string | null): void;

    createUrl(): string;
}

/**
 * This function generates a URL pointing to an image.
 *
 * @example-ref examples/portal/imageUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.id ID of the image content.
 * @param {string} params.path Path to the image. If `id` is specified, this parameter is not used.
 * @param {string} params.scale Required. Options are width(px), height(px), block(width,height) and square(px).
 * @param {number} [params.quality=85] Quality for JPEG images, ranges from 0 (max compression) to 100 (min compression).
 * @param {string} [params.background] Background color.
 * @param {string} [params.format] Format of the image.
 * @param {string} [params.filter] A number of filters are available to alter the image appearance, for example, blur(3), grayscale(), rounded(5), etc.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {string} [params.projectName] Name of the project.
 * @param {string} [params.branch] Name of the branch.
 * @param {string} [params.baseUrlKey] Key of the content.
 * @param {boolean} [params.offline=false] Set to true if the URL should be generated without context of the current request.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function imageUrl(params: ImageUrlParams): string {
    const bean: ImageUrlHandler = __.newBean<ImageUrlHandler>('com.enonic.xp.lib.portal.url.ImageUrlHandler');

    checkRequired(params, 'scale');

    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));
    bean.setUrlType(params.type || 'server');
    bean.setQueryParams(__.toScriptValue(params.params));
    bean.setBackground(__.nullOrValue(params.background));
    bean.setQuality(__.nullOrValue(params.quality));
    bean.setFilter(__.nullOrValue(params.filter));
    bean.setFormat(__.nullOrValue(params.format));
    bean.setScale(params.scale);
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setBaseUrlKey(__.nullOrValue(params.baseUrlKey));
    bean.setOffline(params.offline || false);

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
    createUrl(value: object): string;
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
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function componentUrl(params: ComponentUrlParams): string {
    const bean: ComponentUrlHandler = __.newBean<ComponentUrlHandler>('com.enonic.xp.lib.portal.url.ComponentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
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
    baseUrlKey?: string;
    offline?: boolean;
}

interface AttachmentUrlHandler {
    setId(value?: string | null): void;

    setPath(value?: string | null): void;

    setUrlType(value?: string | null): void;

    setQueryParams(value?: ScriptValue | null): void;

    setName(value?: string | null): void;

    setLabel(value?: string | null): void;

    setProjectName(value?: string | null): void;

    setBranch(value?: string | null): void;

    setBaseUrlKey(value?: string | null): void;

    setOffline(value: boolean): void;

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
 * @param {string} [params.projectName] Name of the project.
 * @param {string} [params.branch] Name of the branch.
 * @param {string} [params.baseUrlKey] Key of the content.
 * @param {boolean} [params.offline=false] Set to true if the URL should be generated without context of the current request.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function attachmentUrl(params: AttachmentUrlParams): string {
    const bean: AttachmentUrlHandler = __.newBean<AttachmentUrlHandler>('com.enonic.xp.lib.portal.url.AttachmentUrlHandler');

    bean.setId(__.nullOrValue(params.id));
    bean.setPath(__.nullOrValue(params.path));
    bean.setUrlType(params.type || 'server');
    bean.setName(__.nullOrValue(params.name));
    bean.setLabel(__.nullOrValue(params.label));
    bean.setProjectName(__.nullOrValue(params.project));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setBaseUrlKey(__.nullOrValue(params.baseUrlKey));
    bean.setOffline(params.offline || false);
    bean.setDownload(params.download || false);
    bean.setQueryParams(__.toScriptValue(params.params));

    return bean.createUrl();
}

export type PageUrlParams = IdXorPath & {
    type?: 'server' | 'absolute';
    params?: object;
};

interface PageUrlHandler {
    createUrl(value: object): string;
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
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function pageUrl(params: PageUrlParams): string {
    const bean: PageUrlHandler = __.newBean<PageUrlHandler>('com.enonic.xp.lib.portal.url.PageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ServiceUrlParams {
    service: string;
    application?: string;
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
}

interface ServiceUrlHandler {
    createUrl(value: object): string;
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
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function serviceUrl(params: ServiceUrlParams): string {
    const bean: ServiceUrlHandler = __.newBean<ServiceUrlHandler>('com.enonic.xp.lib.portal.url.ServiceUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface IdProviderUrlParams {
    idProvider?: string;
    contextPath?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface IdProviderUrlHandler {
    createUrl(value: object): string;
}

/**
 * This function generates a URL pointing to an ID provider.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.idProvider] Key of an ID provider.
 * If idProvider is not set, then the id provider corresponding to the current execution context will be used.
 * @param {string} [params.contextPath=vhost] Context path. Either `vhost` (using vhost target path) or `relative` to the current path.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function idProviderUrl(params: IdProviderUrlParams): string {
    const bean: IdProviderUrlHandler = __.newBean<IdProviderUrlHandler>('com.enonic.xp.lib.portal.url.IdProviderUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface LoginUrlParams {
    idProvider?: string;
    redirect?: string;
    contextPath?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface LoginUrlHandler {
    createUrl(value: object): string;
}

/**
 * This function generates a URL pointing to the login function of an ID provider.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.idProvider] Key of the id provider using an application.
 * If idProvider is not set, then the id provider corresponding to the current execution context will be used.
 * @param {string} [params.redirect] The URL to redirect to after the login.
 * @param {string} [params.contextPath=vhost] Context path. Either `vhost` (using vhost target path) or `relative` to the current path.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function loginUrl(params: LoginUrlParams): string {
    const bean: LoginUrlHandler = __.newBean<LoginUrlHandler>('com.enonic.xp.lib.portal.url.LoginUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface LogoutUrlParams {
    redirect?: string;
    contextPath?: string;
    type?: 'server' | 'absolute';
    params?: object;
}

interface LogoutUrlHandler {
    createUrl(value: object): string;
}

/**
 * This function generates a URL pointing to the logout function of the application corresponding to the current user.
 *
 *
 * @param {object} [params] Input parameters as JSON.
 * @param {string} [params.redirect] The URL to redirect to after the logout.
 * @param {string} [params.contextPath=vhost] Context path. Either `vhost` (using vhost target path) or `relative` to the current path.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function logoutUrl(params: LogoutUrlParams): string {
    const bean: LogoutUrlHandler = __.newBean<LogoutUrlHandler>('com.enonic.xp.lib.portal.url.LogoutUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface UrlParams {
    path: string;
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
}

interface UrlHandler {
    createUrl(value: object): string;
}

/**
 * This function generates a URL.
 *
 * @example-ref examples/portal/url.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.path Path of the resource.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function url(params: UrlParams): string {
    const bean: UrlHandler = __.newBean<UrlHandler>('com.enonic.xp.lib.portal.url.UrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ProcessHtmlParams {
    value: string;
    type?: 'server' | 'absolute';
    imageWidths?: number[];
    imageSizes?: string;
}

interface ProcessHtmlHandler {
    createUrl(value: object): string;
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
    return bean.createUrl(__.toScriptValue(params));
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
 * @returns {string|null} The current id provider as JSON.
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

export interface MultipartForm {
    [key: string]: MultipartItem | MultipartItem[];
}

interface MultipartHandler {
    getForm(): MultipartForm;

    getItem(name: string, index: number): MultipartItem | null;

    getBytes(name: string, index: number): ByteSource | null;

    getText(name: string, index: number): string | null;
}

/**
 * This function returns a JSON containing multipart items. If not a multipart request, then this function returns `undefined`.
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
 * This function returns a JSON containing a named multipart item. If the item does not exists, it returns `undefined`.
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
 * This function returns a data-stream for a named multipart item.
 *
 * @example-ref examples/portal/getMultipartStream.js
 *
 * @param {string} name Name of the multipart item.
 * @param {number} [index] Optional zero-based index. It should be specified if there are multiple items with the same name.
 *
 * @returns {*} Stream of multipart item data.
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
    setWidth(value?: number): void;

    setHeight(value?: number): void;

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
    application: string;
    api?: string;
    type?: 'server' | 'absolute' | 'websocket';
    params?: object;
    path?: string | string[];
}

interface ApiUrlHandler {
    setPath(value: string | ScriptValue): void;

    createUrl(value: object): string;
}

/**
 * This function generates a URL pointing to a Universal API.
 *
 * @example-ref examples/portal/apiUrl.js
 *
 * @param {object} urlParams Input parameters as JSON.
 * @param {string} urlParams.application Application to reference to the API.
 * @param {string} [urlParams.api] Name of the API
 * @param {string} [urlParams.type=server] URL type. Either `server` (server-relative URL) or `absolute` or `websocket`.
 * @param {string|string[]} [urlParams.path] Path(s) to be appended to the base URL following the api segment to complete request URL.
 * @param {object} [urlParams.params] Custom parameters to append to the URL.
 *
 * @returns {string} The generated URL.
 */
export function apiUrl(urlParams: ApiUrlParams): string {
    checkRequired(urlParams, 'application');

    const {
        application,
        api,
        type = 'server',
        path,
        params,
    } = urlParams ?? {};

    const bean: ApiUrlHandler = __.newBean<ApiUrlHandler>('com.enonic.xp.lib.portal.url.ApiUrlHandler');
    if (path) {
        if (Array.isArray(path)) {
            bean.setPath(__.toScriptValue(path));
        } else {
            bean.setPath(path);
        }
    }

    return bean.createUrl(__.toScriptValue({
        application,
        api,
        type,
        params,
    }));
}
