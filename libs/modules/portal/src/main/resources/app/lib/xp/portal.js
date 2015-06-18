var service = __.getBean('com.enonic.xp.lib.portal.url.UrlService');

exports.assetUrl = function (params) {
    return service.createAssetUrl(params);
};

exports.imageUrl = function (params) {
    return service.createImageUrl(params);
};

exports.componentUrl = function (params) {
    return service.createComponentUrl(params);
};

exports.attachmentUrl = function (params) {
    return service.createServiceUrl(params);
};

exports.pageUrl = function (params) {
    return service.createPageUrl(params);
};

exports.serviceUrl = function (params) {
    return service.createServiceUrl(params);
};

exports.processHtml = function (params) {
    return service.createProcessUrl(params);
};
