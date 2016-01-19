var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var assetsUri = uriScriptHelper.ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('home.html');

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: portal.assetUrl({path: ""}),
        xpVersion: app.version,
        appId: 'home',
        installation: uriScriptHelper.getInstallation()
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;