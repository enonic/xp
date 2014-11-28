function getMethod(req) {
    return req.method;
}

function getContentName(req) {
    return req.content.name;
}

function getPageTemplateKey(req) {
    return req.content.page.template;
}

function getThumbnailSize(req) {
    return req.content.thumbnail.size;
}

exports.get = function (req) {
    var body = getMethod(req);
    body += ',' + getContentName(req);
    body += ',' + getPageTemplateKey(req);
    body += ',' + getThumbnailSize(req);

    return {
        body: body
    };
};
