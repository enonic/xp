function getMethod(req) {
    return req.method;
}

function getContentName(req) {
    return req.content._name;
}

function getPageTemplateKey(req) {
    return req.content.page.template;
}

exports.get = function (req) {
    var body = getMethod(req);
    body += ',' + getContentName(req);
    body += ',' + getPageTemplateKey(req);

    return {
        body: body
    };
};
