var admin = require('/lib/xp/admin');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function getPhrases() {
    return JSON.stringify(i18n.getPhrases(admin.getLocale()));
}

function handleGet() {
    var view = resolve('./main.html');

    var params = {
        adminUrl: admin.getBaseUri(),
        adminAssetsUri: admin.getAssetsUri(),
        assetsUri: portal.assetUrl({
            path: ''
        }),
        appName: 'Users',
        appId: app.name,
        xpVersion: app.version,
        messages: admin.getPhrases()
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}

exports.get = handleGet;
