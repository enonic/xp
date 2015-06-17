exports.content = {};

/**
* Get content by key (path or id)
* @param {String} key of the content to get
* @return {Content}
*/
exports.content.get = function (key) {
    var content;
    if (typeof key == 'undefined') {
        content = execute('portal.getContent');
    }
    else {
        content = execute('content.get', {
            key: key
        });
    }
    return content;
};

/**
* Check if a content exists at the given path.
* @param {String} path of the content to check.
* @return {Boolean} true if a content exists there.
*/
exports.content.exists = function(path) {
    return exports.content.get(path) ? true : false;
};

/**
 * Returns the value of the specified property for the content with the specified key.
 * @param {String} Key of the content.
 * @param {String} property to be accessed.
 * @return {String} value of the property.
 */
exports.content.getProperty = function(key, property) {
    if (!key || !property) {
        return null;
    }
    var result = exports.content.get(key);
    return result ? result[property] : null;
};

/**
 * Returns the path to the content location. If the key to a content is passed, it will be used. If contenKey is null, the path
 * to the page that the part is on will be returned unless noDefault is true.
 * @param {Content} content key. Example: content._id
 * @param {Boolean} force null return if no content found with the key
 * @return {String} Returns the path of the content.
 */
exports.content.getPath = function(contentKey, noDefault) {
    var defaultContent = '';
    if(noDefault) {
        defaultContent._path = null;
    } else {
        defaultContent = execute('portal.getContent');
    }

    var contentPath;
    if (contentKey) {
        var content = exports.content.get(contentKey);
        if (content) {
            contentPath = content._path;
        }
    }
    return contentPath ? contentPath : defaultContent._path;
};

/**
* Returns the parent path of the content path that is passed.
* @param {String} path of the content to check.
* @Return {String} path of the parent content.
*/
exports.content.getParentPath = function(path) {
    var pathArray = path.split('/')
    return pathArray.slice(0, pathArray.length -1).join('/');
};

/**
* Returns the parent of the content id or path that is passed.
* @param {String} path or id of the content.
* @Return {Object} the parent content.
*/
exports.content.getParent = function(key) {
    var content = exports.content.get(key);
    var parentPath = exports.content.getParentPath(content._path);
    return parentPath.length < 1 ? null : exports.content.get(parentPath);
};