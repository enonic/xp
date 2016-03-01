var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUri = uriScriptHelper.generateAdminUri();
    var assetsUri = uriScriptHelper.generateAdminAssetsUri();
    var view = resolve('../common/admin-app.html');

    var params = {
        adminUri: adminUri,
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: portal.assetUrl({path: ""}),
        xpVersion: app.version,
        appId: 'content-studio',
        appName: 'Content Studio'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;