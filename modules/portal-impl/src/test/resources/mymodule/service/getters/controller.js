function getMethod(req) {
    return req.method;
}

function getBaseUri(req) {
    return req.baseUri;
}

function getRenderMode(req) {
    return req.mode;
}

exports.get = function (req) {
    var body = getMethod(req);
    body += ',' + getBaseUri(req);
    body += ',' + getRenderMode(req);

    return {
        body: body
    };
};
