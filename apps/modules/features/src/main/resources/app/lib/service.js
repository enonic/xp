exports.service = {};

exports.service.serviceUrl = function (service, params, module) {
    var url;
    if (params && module) {
        url = execute('portal.serviceUrl', {
            service: service,
            params: params,
            module: module
        });
    } else if (params) {
        url = execute('portal.serviceUrl', {
            service: service,
            params: params
        });
    } else if(module) {
        url = execute('portal.serviceUrl', {
            service: service,
            module: module
        });
    } else {
        url = execute('portal.serviceUrl', {
            service: service
        });
    }
    return url;
};