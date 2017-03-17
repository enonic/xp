var mustache = require('/lib/xp/mustache');

function handleGet() {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUrl = uriScriptHelper.generateAdminUri();
    var adminAssetsUri = uriScriptHelper.generateAdminAssetsUri();
    var assetsUri = adminAssetsUri + '/apps/content-studio';
    var view = resolve('./content-studio.html');
    
    var params = {
        adminUrl: adminUrl,
        adminAssetsUri: adminAssetsUri,
        assetsUri: assetsUri,
        baseUri: '',
        xpVersion: app.version,
        appId: app.name,
        appName: 'Content Studio'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;