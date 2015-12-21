var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function handleGet(req) {
    var view = resolve('login.html');

    var params = {
        assetsUri: '/admin/assets/0',
        baseUri: '',
        xpVersion: '6.4.0-SNAPSHOT'
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;