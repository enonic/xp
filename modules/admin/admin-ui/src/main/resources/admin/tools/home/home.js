var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var assetsUri = uriScriptHelper.ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('home.html');

    var isLatestSnapshot = app.version.endsWith('.0.SNAPSHOT');
    var docLinkPrefix = 'http://xp.readthedocs.org/en/';

    if (isLatestSnapshot) {
        docLinkPrefix += 'latest';
    } else {
        var versionParts = app.version.split('.');
        docLinkPrefix += versionParts[0] + '.' + versionParts[1];
    }

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        portalAssetsUrl: portal.assetUrl({path: ""}),
        xpVersion: app.version.replace('.SNAPSHOT', ''),
        docLinkPrefix: docLinkPrefix
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}

exports.get = handleGet;
