var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');

exports.handle403 = function (req) {

    log.info(JSON.stringify(req, null, 2));

    var authConfig = portalLib.getAuthConfig();

    var view = resolve('auth.html');
    var params = {
        userStore: authConfig.userStore,
        backgroundUrl: portalLib.assetUrl({
            application: authConfig.background.application,
            path: authConfig.background.path
        })
    };
    var body = mustacheLib.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}
