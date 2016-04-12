var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');
var authLib = require('/lib/xp/auth');

exports.handle403 = function (req) {
    var userStore = authLib.getUserStore();

    var jQueryUrl = portalLib.assetUrl({path: "js/jquery-2.2.0.min.js"});
    var appLoginJsUrl = portalLib.assetUrl({path: "js/simple-auth.js"});
    var appLoginCssUrl = portalLib.assetUrl({path: "common/styles/_all.css"});
    var appLoginServiceUrl = portalLib.serviceUrl({service: "login"});
    var idProviderConfig = authLib.getIdProviderConfig();


    //Retrieves the title
    var title = idProviderConfig.title || "Enonic XP - Login";

    //Retrieves the background URL
    var backgroundUrl;
    if (idProviderConfig.background) {
        backgroundUrl = portalLib.assetUrl({
            application: idProviderConfig.background.application,
            path: idProviderConfig.background.path
        });
    } else {
        backgroundUrl = portalLib.assetUrl({path: "common/images/background-1920.jpg"});
    }

    //Retrieves the branding URL
    var brandingUrl;
    if (idProviderConfig.branding) {
        brandingUrl = portalLib.assetUrl({
            application: idProviderConfig.branding.application,
            path: idProviderConfig.branding.path
        });
    } else {
        brandingUrl = portalLib.assetUrl({path: "common/images/enonic.svg"});
    }


    var view = resolve('identity.html');
    var params = {
        userStoreKey: userStore.key,
        jQueryUrl: jQueryUrl,
        appLoginJsUrl: appLoginJsUrl,
        appLoginCssUrl: appLoginCssUrl,
        appLoginServiceUrl: appLoginServiceUrl,
        title: title,
        backgroundUrl: backgroundUrl,
        brandingUrl: brandingUrl
    };
    var body = mustacheLib.render(view, params);

    return {
        status: 403,
        contentType: 'text/html',
        body: body
    };
};

exports.logout = function (req) {
    authLib.logout();
    if (req.params.redirect) {
        return {
            redirect: req.params.redirect
        }
    }
};