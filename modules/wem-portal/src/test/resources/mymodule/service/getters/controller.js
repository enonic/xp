function getMethod(context) {
    return context.request.method;
}

function getContentName(context) {
    return context.content.name;
}

function getPageTemplateKey(context) {
    return context.content.getPage().template;
}

function getThumbnailSize(context) {
    return context.content.thumbnail.size;
}

exports.get = function (context) {
    var body = getMethod(context);
    body += ',' + getContentName(context);
    body += ',' + getPageTemplateKey(context);
    body += ',' + getThumbnailSize(context);

    context.response.body = body;
};
