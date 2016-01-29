var mustacheLib = require('/lib/xp/mustache');
var portalLib = require('/lib/xp/portal');

exports.handle403 = function (req) {

    log.info(JSON.stringify(req, null, 2));

    var view = resolve('auth.html');
    var params = {
        backgroundUrl: portalLib.assetUrl({
            path: "img/background.jpg"
        })
    };
    var body = mustacheLib.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}
