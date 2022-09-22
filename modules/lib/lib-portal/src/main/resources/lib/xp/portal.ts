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

    // eslint-disable-next-line @typescript-eslint/no-empty-interface
    interface XpXData {
    }
}

export interface Attachment {
    name: string;
    label?: string;
    size: number;
    mimeType: string;
}

export type Attachments = Record<string, Attachment>;

export type XDataEntry = Record<string, Record<string, unknown>>;

export type XData = Record<string, XDataEntry>;

export type WorkflowState = 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';

export type WorkflowCheckState = 'PENDING' | 'REJECTED' | 'APPROVED';

export type ContentInheritType = 'CONTENT' | 'PARENT' | 'NAME' | 'SORT';

export interface Workflow {
    state: WorkflowState;
    checks?: Record<string, WorkflowCheckState>;
}

export interface PublishInfo {
    from?: string;
    to?: string;
    first?: string;
}

export interface Content<Data = Record<string, unknown>, Type extends string = string> {
    _id: string;
    _name: string;
    _path: string;
    _score: number;
    creator: string;
    modifier: string;
    createdTime: string;
    modifiedTime: string;
    owner: string;
    data: Data;
    type: Type;
    displayName: string;
    hasChildren: boolean;
    language: string;
    valid: boolean;
    originProject: string;
    childOrder?: string;
    _sort?: object[];
    x: XpXData;
    attachments: Attachments;
    publish?: PublishInfo;
    workflow?: Workflow;
    inherit?: ContentInheritType[];
}

export type Site<Config> = Content<{
    description?: string;
    siteConfig: SiteConfig<Config> | SiteConfig<Config>[];
}, 'portal:site'>;

export interface SiteConfig<Config> {
    applicationKey: string;
    config: Config;
}

export interface AssetUrlParams {
    path: string;
    application?: string;
    type?: string;
    params?: object;
}

interface AssetUrlHandler {
    createUrl(value: object): string;
}

/**
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
    const bean = __.newBean<AssetUrlHandler>('com.enonic.xp.lib.portal.url.AssetUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ImageUrlParams {
    id: string;
    path: string;
    scale: string;
    quality?: number;
    background?: string;
    format?: string;
    filter?: string;
    server?: string;
    params?: object;
}

interface ImageUrlHandler {
    createUrl(value: object): string;
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
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function imageUrl(params: ImageUrlParams): string {
    const bean = __.newBean<ImageUrlHandler>('com.enonic.xp.lib.portal.url.ImageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ComponentUrlParams {
    id?: string;
    path?: string;
    component?: string;
    type?: string;
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
    const bean = __.newBean<ComponentUrlHandler>('com.enonic.xp.lib.portal.url.ComponentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface AttachmentUrlParams {
    id?: string;
    path?: string;
    name?: string;
    label?: string;
    download?: boolean;
    type?: string;
    params?: object;
}

interface AttachmentUrlHandler {
    createUrl(value: object): string;
}

/**
 * This function generates a URL pointing to an attachment.
 *
 * @example-ref examples/portal/attachmentUrl.js
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] Id to the content holding the attachment.
 * @param {string} [params.path] Path to the content holding the attachment.
 * @param {string} [params.name] Name to the attachment.
 * @param {string} [params.label=source] Label of the attachment.
 * @param {boolean} [params.download=false] Set to true if the disposition header should be set to attachment.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function attachmentUrl(params: AttachmentUrlParams): string {
    const bean = __.newBean<AttachmentUrlHandler>('com.enonic.xp.lib.portal.url.AttachmentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface PageUrlParams {
    id?: string;
    path?: string;
    type?: string;
    params?: object;
}

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
    const bean = __.newBean<PageUrlHandler>('com.enonic.xp.lib.portal.url.PageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ServiceUrlParams {
    service: string;
    application?: string;
    type?: string;
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
    const bean = __.newBean<ServiceUrlHandler>('com.enonic.xp.lib.portal.url.ServiceUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface IdProviderUrlParams {
    idProvider?: string;
    contextPath?: string;
    type?: string;
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
    const bean = __.newBean<IdProviderUrlHandler>('com.enonic.xp.lib.portal.url.IdProviderUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface LoginUrlParams {
    idProvider?: string;
    redirect?: string;
    contextPath?: string;
    type?: string;
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
 * @param {string} [params.idProvider] Key of a id provider using an application.
 * If idProvider is not set, then the id provider corresponding to the current execution context will be used.
 * @param {string} [params.redirect] The URL to redirect to after the login.
 * @param {string} [params.contextPath=vhost] Context path. Either `vhost` (using vhost target path) or `relative` to the current path.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
export function loginUrl(params: LoginUrlParams): string {
    const bean = __.newBean<LoginUrlHandler>('com.enonic.xp.lib.portal.url.LoginUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface LogoutUrlParams {
    redirect?: string;
    contextPath?: string;
    type?: string;
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
    const bean = __.newBean<LogoutUrlHandler>('com.enonic.xp.lib.portal.url.LogoutUrlHandler');
    return bean.createUrl(__.toScriptValue(params ?? {}));
}

export interface UrlParams {
    path: string;
    type?: string;
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
    const bean = __.newBean<UrlHandler>('com.enonic.xp.lib.portal.url.UrlHandler');
    return bean.createUrl(__.toScriptValue(params));
}

export interface ProcessHtmlParams {
    value: string;
    type?: string;
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
    const bean = __.newBean<ProcessHtmlHandler>('com.enonic.xp.lib.portal.url.ProcessHtmlHandler');
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
    const bean = __.newBean<SanitizeHtmlHandler>('com.enonic.xp.lib.portal.SanitizeHtmlHandler');
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
    const bean = __.newBean<GetCurrentSiteHandler>('com.enonic.xp.lib.portal.current.GetCurrentSiteHandler');
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
    const bean = __.newBean<GetCurrentSiteConfigHandler>('com.enonic.xp.lib.portal.current.GetCurrentSiteConfigHandler');
    return __.toNativeObject(bean.execute<Config>());
}

interface GetCurrentContentHandler {
    execute<Data, Type extends string>(): Content<Data, Type> | null;
}

/**
 * This function returns the content corresponding to the current execution context. It is meant to be called from a page, layout or
 * part controller
 *
 * @example-ref examples/portal/getContent.js
 *
 * @returns {object|null} The current content as JSON.
 */
export function getContent<Data = Record<string, unknown>, Type extends string = string>(): Content<Data, Type> | null {
    const bean = __.newBean<GetCurrentContentHandler>('com.enonic.xp.lib.portal.current.GetCurrentContentHandler');
    return __.toNativeObject(bean.execute<Data, Type>());
}

export interface Component<Config extends object = object, Regions extends Record<string, Region> = Record<string, Region>> {
    config: Config;
    descriptor: string;
    path: string;
    type: 'page' | 'layout' | 'part';
    regions: Regions;
}

export interface Region<Config extends object = object> {
    components: Component<Config>[];
}

interface GetCurrentComponentHandler<Config extends object = object,
    Regions extends Record<string, Region> = Record<string, Region>> {

    execute(): Component<Config, Regions> | null;
}


/**
 * This function returns the component corresponding to the current execution context. It is meant to be called
 * from a layout or part controller.
 *
 * @example-ref examples/portal/getComponent.js
 *
 * @returns {object|null} The current component as JSON.
 */
export function getComponent<Config extends object = object,
    Regions extends Record<string, Region> = Record<string, Region>,
    >(): Component<Config, Regions> | null {
    const bean = __.newBean<GetCurrentComponentHandler<Config, Regions>>('com.enonic.xp.lib.portal.current.GetCurrentComponentHandler');
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
    const bean = __.newBean<GetCurrentIdProviderKeyHandler>('com.enonic.xp.lib.portal.current.GetCurrentIdProviderKeyHandler');
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

    getBytes(name: string, index: number): object | null;

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
    const bean = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
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
export function getMultipartItem(name: string, index: number): MultipartItem | null {
    const bean = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return __.toNativeObject(bean.getItem(name, index ?? 0));
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
export function getMultipartStream(name: string, index: number): object | null {
    const bean = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getBytes(name, index ?? 0);
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
export function getMultipartText(name: string, index: number): string | null {
    const bean = __.newBean<MultipartHandler>('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getText(name, index ?? 0);
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
    const bean = __.newBean<ImagePlaceholderHandler>('com.enonic.xp.lib.portal.url.ImagePlaceholderHandler');
    bean.setWidth(params?.width ?? 0);
    bean.setHeight(params?.height ?? 0);
    return bean.createImagePlaceholder();
}
