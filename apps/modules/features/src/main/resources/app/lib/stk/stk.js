var portal = require('/lib/xp/portal');

exports.data = require('data.js').data;
exports.content = require('content.js').content;
exports.view = require('view.js').view;

exports.log = function (data) {
    log.info('STK log %s', JSON.stringify(data, null, 4));
};

exports.serviceUrl = function (service, params, module) {
    var url;
    if (params && module) {
        url = portal.serviceUrl({
            service: service,
            params: params,
            module: module
        });
    } else if (params) {
        url = portal.serviceUrl({
            service: service,
            params: params
        });
    } else if(module) {
        url = portal.serviceUrl({
            service: service,
            module: module
        });
    } else {
        url = portal.serviceUrl({
            service: service
        });
    }
    return url;
};