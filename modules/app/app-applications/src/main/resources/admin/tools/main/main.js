var admin = require('/lib/xp/admin');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet() {
    var view = resolve('./main.html');

    var params = {
        adminUrl: admin.getBaseUri(),
        adminAssetsUri: admin.getAssetsUri(),
        assetsUri: portal.assetUrl({
            path: ''
        }),
        appName: 'Applications',
        appId: app.name,
        xpVersion: app.version,
        messages: admin.getPhrases(),
        launcherUrl: admin.getLauncherUrl()
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}

exports.get = handleGet;
