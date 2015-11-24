/**
 * HTTP Client related functions.
 *
 * @example
 * var httpClientLib = require('/lib/xp/http-client');
 *
 * @module lib/xp/http-client
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
 * @property {string} body Body of the response as string.
 */

/**
 * Sends an HTTP request and returns the response received from the remote server.
 * The request is sent synchronously, the execution blocks until the response is received.
 *
 * @example
 * var response = httpClientLib.request({
 *   url: 'http://my-server/some/path',
 *   method: 'post',
 *   headers: {
 *      'X-Custom-Header': 'header-value'
 *   },
 *   connectionTimeout: 20000,
 *   readTimeout: 5000,
 *   body: '{"id": 123}',
 *   contentType: 'application/json'
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.url URL to which the request is sent.
 * @param {string} params.method The HTTP method to use for the request (e.g. "POST", "GET", "PUT"). Optional, default value is 'GET'.
 * @param {object} params.params Query or form parameters to be sent with the request. Optional.
 * @param {object} params.headers HTTP headers, an object where the keys are header names and the values the header values. Optional.
 * @param {number} params.connectionTimeout The timeout on establishing the connection, in milliseconds. Default 10000ms (10s).
 * @param {number} params.readTimeout The timeout on waiting to receive data, in milliseconds. Default 10000ms (10s).
 * @param {string} params.body Body content to send with the request, usually for POST or PUT requests. Optional.
 * @param {string} params.contentType Content type of the request. Optional.
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

    return __.toNativeObject(bean.request());

};
