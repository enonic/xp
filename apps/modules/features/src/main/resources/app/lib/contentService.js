exports.getChildren = function (parentPath, size) {

    return execute('content.getChildren', {
        key: parentPath,
        start: 0,
        count: size ? size : 500
    });
};

exports.getContentById = function (contentId) {

    return execute('content.get', {
        key: contentId
    });

};
