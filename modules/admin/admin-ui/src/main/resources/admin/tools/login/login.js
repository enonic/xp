var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var assetsUri = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper").ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('../common/admin-app.html');

    var config = [];
    if (req.params.callback) {
        config.push({
            key: 'callback',
            value: req.params.callback
        });
    }

    var params = {
        config: config,
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: '',
        xpVersion: app.version,
        app: 'login',
        appName: 'Login'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;