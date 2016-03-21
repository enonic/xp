var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');
var authLib = require('/lib/xp/auth');

exports.handle403 = function (req) {
    var userStore = authLib.getUserStore();

    var jQueryUrl = portalLib.assetUrl({path: "js/jquery-2.2.0.min.js"});
    var appLoginJsUrl = portalLib.assetUrl({path: "js/app-login.js"});
    var appLoginCssUrl = portalLib.assetUrl({path: "css/app-login.css"});

    var backgroundUrl;
    var idProviderConfig = authLib.getIdProviderConfig();
    if (idProviderConfig.background) {
        backgroundUrl = portalLib.assetUrl({
            application: idProviderConfig.background.application,
            path: idProviderConfig.background.path
        });
    } else {
        backgroundUrl = portalLib.assetUrl({path: "img/background-1920.jpg"});
    }

    var view = resolve('auth.html');
    var params = {
        userStoreKey: userStore.key,
        jQueryUrl: jQueryUrl,
        appLoginJsUrl: appLoginJsUrl,
        appLoginCssUrl: appLoginCssUrl,
        backgroundUrl: backgroundUrl
    };
    var body = mustacheLib.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}
