var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');
var admin = require('/lib/xp/admin');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUrl = uriScriptHelper.generateAdminUri();
    var assetsUri = uriScriptHelper.generateAdminAssetsUri();
    var backgroundUri = uriScriptHelper.generateBackgroundUri();
    var view = resolve('home.html');

    var busIconUrl = portal.assetUrl({path: "icons/bus.svg"});
    var infoIconUrl = portal.assetUrl({path: "icons/info-with-circle.svg"});
    var docsIconUrl = portal.assetUrl({path: "icons/docs.svg"});
    var forumIconUrl = portal.assetUrl({path: "icons/discuss.svg"});
    var marketIconUrl = portal.assetUrl({path: "icons/market.svg"});
    
    var isLatestSnapshot = app.version.endsWith('.0.SNAPSHOT');
    var docLinkPrefix = 'http://docs.enonic.com/en/';

    if (isLatestSnapshot) {
        docLinkPrefix += 'latest';
    } else {
        var versionParts = app.version.split('.');
        docLinkPrefix += versionParts[0] + '.' + versionParts[1];
    }

    var params = {
        adminUrl: adminUrl,
        assetsUri: assetsUri,
        backgroundUri: backgroundUri,
        busIconUrl: busIconUrl,
        infoIconUrl: infoIconUrl,
        docsIconUrl: docsIconUrl,
        forumIconUrl: forumIconUrl,
        marketIconUrl: marketIconUrl,
        baseUri: '',
        xpVersion: app.version,
        docLinkPrefix: docLinkPrefix,
        messages: admin.getPhrases()
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}

exports.get = handleGet;
