var urlService = __.getBean('com.enonic.xp.lib.portal.url.UrlServiceWrapper');
var currentService = __.getBean('com.enonic.xp.lib.portal.current.PortalServiceWrapper');

exports.assetUrl = function (params) {
    return urlService.assetUrl(__.toScriptValue(params));
};

exports.imageUrl = function (params) {
    return urlService.imageUrl(__.toScriptValue(params));
};

exports.componentUrl = function (params) {
    return urlService.componentUrl(__.toScriptValue(params));
};

exports.attachmentUrl = function (params) {
    return urlService.attachmentUrl(__.toScriptValue(params));
};

exports.pageUrl = function (params) {
    return urlService.pageUrl(__.toScriptValue(params));
};

exports.serviceUrl = function (params) {
    return urlService.serviceUrl(__.toScriptValue(params));
};

exports.processHtml = function (params) {
    return urlService.processHtml(__.toScriptValue(params));
};

//

exports.getContent = function () {
    //return __.toNativeObject(currentService.currentContent());
    return currentService.currentContent();
};

exports.getComponent = function () {
    //return __.toNativeObject(currentService.currentComponent());
    return currentService.currentComponent();
};

exports.getSite = function () {
    //return __.toNativeObject(currentService.currentSite());
    return currentService.currentSite();
};

