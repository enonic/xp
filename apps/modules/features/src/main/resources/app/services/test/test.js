var portal = require('/lib/xp/portal');

function handleGet(req) {

    var site = portal.getSite();

    return {
        contentType: 'application/json',
        body: site
    };
}

exports.get = handleGet;
