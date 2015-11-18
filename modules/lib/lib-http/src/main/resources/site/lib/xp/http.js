/**
 * HTTP related functions.
 *
 * @example
 * var httpLib = require('/lib/xp/http');
 *
 * @module lib/xp/http
 */

function checkRequired(params, name) {
    if (params[name] === undefined) {
        throw "Parameter '" + name + "' is required";
    }
}

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
