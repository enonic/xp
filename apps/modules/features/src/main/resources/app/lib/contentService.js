var contentSvc = require('/lib/xp/content');

exports.getChildren = function (parentPath, size) {

    return contentSvc.getChildren({
        key: parentPath,
        start: 0,
        count: size ? size : 500
    });
};

exports.getContentById = function (contentId) {

    return contentSvc.get({
        key: contentId
    });

};
