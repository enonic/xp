var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');
var authLib = require('/lib/xp/auth');

exports.handle403 = function (req) {
    if (/^\/admin\/rest\//.test(req.path)) {
        return null;
    }
    var body = generateLoginPage();

    return {
        status: 403,
        contentType: 'text/html',
        body: body
    };
};

exports.login = function (req) {
    var body = generateLoginPage(req.params.redirect);

    return {
        status: 200,
        contentType: 'text/html',
        body: body
    };
}

exports.logout = function (req) {
    authLib.logout();
    return {
        redirect: req.params.redirect
    };
};

function generateLoginPage(redirectUrl) {
    var jQueryUrl = portalLib.assetUrl({path: "js/jquery-2.2.0.min.js"});
    var appLoginJsUrl = portalLib.assetUrl({path: "js/app-system.js"});
    var appLoginCssUrl = portalLib.assetUrl({path: "common/styles/_all.css"});
    var appLoginBackgroundUrl = portalLib.assetUrl({path: "common/images/background-1920.jpg"});
    var appLoginServiceUrl = portalLib.serviceUrl({service: "login"});

    var configView = resolve('idprovider-config.txt');
    var config = mustacheLib.render(configView, {
        appLoginServiceUrl: appLoginServiceUrl,
        redirectUrl: redirectUrl
    });

    var view = resolve('idprovider.html');
    var params = {
        jQueryUrl: jQueryUrl,
        appLoginJsUrl: appLoginJsUrl,
        appLoginCssUrl: appLoginCssUrl,
        appLoginBackgroundUrl: appLoginBackgroundUrl,
        config: config
    };
    return mustacheLib.render(view, params);
}