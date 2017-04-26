
/**
 * HTTP Client related functions.
 *
 * @example
 * var httpClientLib = require('/lib/xp/http-client');
 *
 * @module http-client
 */

function checkRequired(params, name) {
    if (params[name] === undefined) {
        throw "Parameter '" + name + "' is required";
    }
}

/**
 * @typedef Response
 * @type Object
 * @property {number} status HTTP status code returned.
 * @property {string} message HTTP status message returned.
 * @property {object} headers HTTP headers of the response.
 * @property {string} contentType Content type of the response.
 * @property {string} body Body of the response as string. Null if the response content-type is not of type text.
 * @property {*} bodyStream Body of the response as a stream object.
 */

/**
 * Sends an HTTP request and returns the response received from the remote server.
 * The request is sent synchronously, the execution blocks until the response is received.
 *
 * @example-ref examples/http-client/request.js
 * @example-ref examples/http-client/multipart.js
 * @example-ref examples/http-client/basicauth.js
 * @example-ref examples/http-client/proxy.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.url URL to which the request is sent.
 * @param {string} [params.method=GET] The HTTP method to use for the request (e.g. "POST", "GET", "HEAD", "PUT", "DELETE").
 * @param {object} [params.params] Query or form parameters to be sent with the request.
 * @param {object} [params.headers] HTTP headers, an object where the keys are header names and the values the header values.
 * @param {number} [params.connectionTimeout=10000] The timeout on establishing the connection, in milliseconds.
 * @param {number} [params.readTimeout=10000] The timeout on waiting to receive data, in milliseconds.
 * @param {string|*} [params.body] Body content to send with the request, usually for POST or PUT requests. It can be of type string or stream.
 * @param {string} [params.contentType] Content type of the request.
 * @param {object[]} [params.multipart] Multipart form data to send with the request, an array of part objects. Each part object contains
 * 'name', 'value', and optionally 'fileName' and 'contentType' properties. Where 'value' can be either a string or a Stream object.
 * @param {object} [params.auth] Settings for basic authentication.
 * @param {string} [params.auth.user] User name for basic authentication.
 * @param {string} [params.auth.password] Password for basic authentication.
 * @param {object} [params.proxy] Proxy settings.
 * @param {string} [params.proxy.host] Proxy host name to use.
 * @param {number} [params.proxy.port] Proxy port to use.
 * @param {string} [params.proxy.user] User name for proxy authentication.
 * @param {string} [params.proxy.password] Password for proxy authentication.
 *
 * @return {Response} response HTTP response received.
 */
exports.request = function (params) {

    var bean = __.newBean('com.enonic.xp.lib.http.HttpRequestHandler');

    checkRequired(params, 'url');

    bean.url = __.nullOrValue(params.url);
    bean.params = __.nullOrValue(params.params);
    bean.method = __.nullOrValue(params.method);
    bean.headers = __.nullOrValue(params.headers);
    bean.connectionTimeout = __.nullOrValue(params.connectionTimeout);
    bean.readTimeout = __.nullOrValue(params.readTimeout);
    bean.body = __.nullOrValue(params.body);
    bean.contentType = __.nullOrValue(params.contentType);
    bean.multipart = __.nullOrValue(params.multipart);
    if (params.proxy) {
        bean.proxyHost = __.nullOrValue(params.proxy.host);
        bean.proxyPort = __.nullOrValue(params.proxy.port);
        bean.proxyUser = __.nullOrValue(params.proxy.user);
        bean.proxyPassword = __.nullOrValue(params.proxy.password);
    }
    if (params.auth) {
        bean.authUser = __.nullOrValue(params.auth.user);
        bean.authPassword = __.nullOrValue(params.auth.password);
    }
    return __.toNativeObject(bean.request());

};
