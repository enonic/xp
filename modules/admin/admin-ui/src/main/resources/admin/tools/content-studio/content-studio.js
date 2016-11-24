var mustache = require('/lib/xp/mustache');

function handleGet() {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUrl = uriScriptHelper.generateAdminUri();
    var assetsUri = uriScriptHelper.generateAdminAssetsUri();
    var view = resolve('./content-studio.html');

    var params = {
        adminUrl: adminUrl,
        assetsUri: assetsUri,
        baseUri: '',
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