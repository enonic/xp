exports.assetUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.AssetUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.imageUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ImageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.componentUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ComponentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.attachmentUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.AttachmentUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.pageUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.PageUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.serviceUrl = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ServiceUrlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.processHtml = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.portal.url.ProcessHtmlHandler');
    return bean.createUrl(__.toScriptValue(params));
};

exports.getSite = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentSiteHandler');
    return __.toNativeObject(bean.execute());
};

exports.getContent = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentContentHandler');
    return __.toNativeObject(bean.execute());
};

exports.getComponent = function () {
    var bean = __.newBean('com.enonic.xp.lib.portal.current.GetCurrentComponentHandler');
    return __.toNativeObject(bean.execute());
};
