var mustache = require('/lib/xp/mustache');

function handleGet(req) {
    log.info("Req: " + JSON.stringify(req, null, 2));
    var view = resolve('../common/admin-app.html');
    var params = {
        assetsUri: '/admin/assets/0',
        baseUri: '',
        xpVersion: '6.4.0-SNAPSHOT',
        app: 'content-manager'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;