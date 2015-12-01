/**
 * Functions to access portal functionality.
 *
 * @example
 * var portalLib = require('/lib/xp/portal');
 *
 * @module lib/xp/portal
 */

/**
 * This function generates a URL pointing to a static file.
 *
 * @example
 * var url = portalLib.assetUrl({
 *   path: 'styles/main.css'
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.path Path to the asset.
 * @param {string} [params.application] Other application to reference to. Defaults to current application.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
exports.assetUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.AssetUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function generates a URL pointing to an image.
 *
 * @example
 * var url = portalLib.imageUrl({
 *   id: '1234',
 *   scale: 'block(1024,768)',
 *   filter: 'rounded(5);sharpen()'
 * });
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
exports.imageUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ImageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function generates a URL pointing to a component.
 *
 * @example
 * var url = portalLib.componentUrl({
 *   component: 'main/0'
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} [params.id] Id to the page.
 * @param {string} [params.path] Path to the page.
 * @param {string} [params.component] Path to the component. If not set, the current path is set.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
exports.componentUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ComponentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function generates a URL pointing to an attachment.
 *
 * @example
 * var url = portalLib.attachmentUrl({
 *   id: '1234',
 *   download: true
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.id Id to the content holding the attachment.
 * @param {string} params.path Path to the content holding the attachment.
 * @param {string} [params.name] Name to the attachment.
 * @param {string} [params.type=source] Label of the attachment.
 * @param {boolean} [params.download=false] Set to true if the disposition header should be set to attachment.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
exports.attachmentUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.AttachmentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function generates a URL pointing to a page.
 *
 * @example
 * var url = portalLib.pageUrl({
 *   path: '/my/page',
 *   params: {
 *     a: 1,
 *     b: [1, 2]
 *   }
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.id Id to the page. If id is set, then path is not used.
 * @param {string} params.path Path to the page. Relative paths is resolved using the context page.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
exports.pageUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.PageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function generates a URL pointing to a service.
 *
 * @example
 * var url = portalLib.serviceUrl({
 *   service: '/myservice',
 *   params: {
 *     a: 1,
 *     b: [1, 2]
 *   }
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.name Name of the service.
 * @param {string} [params.application] Other application to reference to. Default is current application.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 * @param {object} [params.params] Custom parameters to append to the url.
 *
 * @returns {string} The generated URL.
 */
exports.serviceUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ServiceUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function replaces abstract internal links contained in an HTML text by generated URLs.
 *
 * When outputting processed HTML in Thymeleaf, use attribute `data-th-utext="${processedHtml}"`.
 *
 * @example
 * var processedHtml = portalLib.processHtml({
 *   value: '<a href="content://123" target="">Content</a>' +
 *          '<a href="media://inline/123" target="">Inline</a>' +
 *          '<a href="media://download/123" target="">Download</a>'
 * });
 *
 * @param {object} params Input parameters as JSON.
 * @param {string} params.value Html value string to process.
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 *
 * @returns {string} The processed HTML.
 */
exports.processHtml = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ProcessHtmlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

/**
 * This function returns the parent site of the content corresponding to the current execution context. It is meant to be
 * called from a page, layout or part controller.
 *
 * @example
 * var result = portalLib.getSite();
 * log.info('Current site %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The current site as JSON.
 */
exports.getSite = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentSiteHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * This function returns the site configuration for this app in the parent site of the content corresponding to the current
 * execution context. It is meant to be called from a page, layout or part controller.
 *
 * @example
 * var result = portalLib.getSiteConfig();
 * log.info('Current site-config %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The site configuration for current application as JSON.
 */
exports.getSiteConfig = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentSiteConfigHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * This function returns the content corresponding to the current execution context. It is meant to be called from a page, layout or
 * part controller
 *
 * @example
 * var result = portalLib.getContent();
 * log.info('Current content %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The current content as JSON.
 */
exports.getContent = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentContentHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * This function returns the component corresponding to the current execution context. It is meant to be called
 * from a layout or part controller.
 *
 * @example
 * var result = portalLib.getComponent();
 * log.info('Current component %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The current component as JSON.
 */
exports.getComponent = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentComponentHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * This function returns a JSON containing multipart items. If not a multipart request, then this function returns `undefined`.
 *
 * @example
 * var result = portalLib.getMultipartForm();
 * log.info('Multipart form %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The multipart form items.
 */
exports.getMultipartForm = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return __.toNativeObject(bean.getForm());
};

/**
 * This function returns a JSON containing a named multipart item. If the item does not exists, it returns `undefined`.
 *
 * @example
 * var result = portalLib.getMultipartItem('file');
 * log.info('Multipart form item %s', JSON.stringify(result, null, 2));
 *
 * @returns {object} The named multipart form item.
 */
exports.getMultipartItem = function (name) {
    var bean = __.newBean('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return __.toNativeObject(bean.getItem(name));
};

/**
 * This function returns a data-stream for a named multipart item.
 *
 * @example
 * var result = portalLib.getMultipartStream('file');
 *
 * @returns {*} Stream of multipart item data.
 */
exports.getMultipartStream = function (name) {
    var bean = __.newBean('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getBytes(name);
};

/**
 * This function returns the multipart item data as text.
 *
 * @example
 * var result = portalLib.getMultipartText('item');
 *
 * @returns {string} Text for multipart item data.
 */
exports.getMultipartText = function (name) {
    var bean = __.newBean('com.enonic.xp.lib.portal.multipart.MultipartHandler');
    return bean.getText(name);
};
