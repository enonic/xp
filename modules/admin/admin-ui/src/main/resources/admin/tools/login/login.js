var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUrl = uriScriptHelper.generateAdminUri();
    var assetsUri = uriScriptHelper.generateAdminAssetsUri();
    var view = resolve('./login.html');

    var config = [];
    if (req.params.callback) {
        config.push({
            key: 'callback',
            value: req.params.callback
        });
    }

    var params = {
        config: config,
        adminUrl: adminUrl,
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: '',
        xpVersion: app.version,
        appId: 'login',
        appName: 'Login'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;