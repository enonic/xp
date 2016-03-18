var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');
var authLib = require('/lib/xp/auth');

exports.handle403 = function (req) {

    log.info(JSON.stringify(req, null, 2));

    var userStore = authLib.getUserStore();

    var idProviderConfig = authLib.getIdProviderConfig();
    var backgroundUrl;
    if (idProviderConfig.background) {
        backgroundUrl = portalLib.assetUrl({
            application: idProviderConfig.background.application,
            path: idProviderConfig.background.path
        });
    } else {
        //TODO merge master backgroundUrl = portalLib.url({path: "/admin/common/images/background-1920.jpg"});
        backgroundUrl = "/admin/common/images/background-1920.jpg";
    }

    var view = resolve('auth.html');
    var params = {
        userStore: userStore.key,
        backgroundUrl: backgroundUrl
    };
    var body = mustacheLib.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}
