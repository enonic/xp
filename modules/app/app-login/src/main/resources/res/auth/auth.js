var mustache = require('/lib/xp/mustache');

exports.handle403 = function (req) {

    log.info(JSON.stringify(req, null, 2));

    var view = resolve('auth.html');
    var body = mustache.render(view, {});

    return {
        contentType: 'text/html',
        body: body
    };
}
