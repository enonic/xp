var service = __.getBean('com.enonic.xp.lib.portal.url.UrlServiceWrapper');

exports.assetUrl = function (params) {
    return service.assetUrl(__.toScriptValue(params));
};

exports.imageUrl = function (params) {
    return service.imageUrl(__.toScriptValue(params));
};

exports.componentUrl = function (params) {
    return service.componentUrl(__.toScriptValue(params));
};

exports.attachmentUrl = function (params) {
    return service.attachmentUrl(__.toScriptValue(params));
};

exports.pageUrl = function (params) {
    return service.pageUrl(__.toScriptValue(params));
};

exports.serviceUrl = function (params) {
    return service.serviceUrl(__.toScriptValue(params));
};

exports.processHtml = function (params) {
    return service.processHtml(__.toScriptValue(params));
};
