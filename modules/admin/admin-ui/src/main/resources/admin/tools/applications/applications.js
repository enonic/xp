var mustache = require('/lib/xp/mustache');

function handleGet(req) {
    var assetsUri = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper").ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('../common/admin-app.html');

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        xpVersion: app.version,
        app: 'applications'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;