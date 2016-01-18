var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var assetsUri = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper").ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('../common/admin-app.html');

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: portal.assetUrl({path: ""}),
        xpVersion: app.version,
        app: 'content-studio',
        appName: 'Content Studio'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;