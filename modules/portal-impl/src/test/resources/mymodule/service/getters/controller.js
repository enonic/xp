function getMethod(req) {
    return req.method;
}

function getRenderMode(req) {
    return req.mode;
}

exports.get = function (req) {
    var body = getMethod(req);
    body += ',' + getRenderMode(req);

    return {
        body: body
    };
};
