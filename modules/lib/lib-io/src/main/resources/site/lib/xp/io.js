/**
 * IO related functions.
 *
 * @example
 * var ioLib = require('/lib/xp/io');
 *
 * @module lib/xp/io
 */

var bean = __.newBean('com.enonic.xp.lib.io.IOHandlerBean');


/**
 * Read text from a stream or string.
 *
 * @example
 * var text = ioLib.readText(stream);
 *
 * @param {*} val Stream to read from.
 * @returns {string} Returns the text read from stream or string.
 */
exports.readText = function (val) {
    return bean.readText(val);
};

/**
 * Read lines from a stream or string.
 *
 * @example
 * var lines = ioLib.readLines(stream);
 *
 * @param {*} val Stream or string to read from.
 * @returns {string} Returns lines as an array.
 */
exports.readLines = function (val) {
    return __.toNativeObject(bean.readLines(val));
};

/**
 * Process lines from a stream or string.
 *
 * @example
 * ioLib.processLines(stream, function(line) {
 *   log.info(line);
 * });
 *
 * @param {*} val Stream or string to read from.
 * @param {function} func Callback function to be called for each line.
 */
exports.processLines = function (val, func) {
    return bean.processLines(val, func);
};

/**
 * Looks up a resource.
 *
 * @param {*} key Resource key to look up.
 * @constructor
 * @private
 */
function Resource(key) {
    this.res = bean.getResource(key);
}

/**
 * Returns the resource stream.
 *
 * @example
 * var stream = res.getStream();
 *
 * @returns Stream of resource.
 */
Resource.prototype.getStream = function () {
    return this.res.getBytes();
};

/**
 * Returns the resource size.
 *
 * @example
 * var size = res.getSize();
 *
 * @returns {number} Size of resource in bytes.
 */
Resource.prototype.getSize = function () {
    return this.res.getSize();
};

/**
 * Returns true if the resource exists.
 *
 * @example
 * var exists = res.exists();
 *
 * @returns {boolean} True if resource exists.
 */
Resource.prototype.exists = function () {
    return this.res.exists();
};

/**
 * Looks up a resource.
 *
 * @example
 * var res = ioLib.getResource('/path/to/myfile.txt');
 * var exists = res.exists();
 * var size = res.getSize();
 * var stream = res.getStream();
 *
 * @example
 * var res = ioLib.getResource(resolve('./myfile.txt'));
 * var exists = res.exists();
 * var size = res.getSize();
 * var stream = res.getStream();
 *
 * @param {*} key Resource key to look up.
 * @returns {Resource} Resource reference.
 */
exports.getResource = function (key) {
    return new Resource(key);
};

/**
 * Returns the size of a stream or a string.
 *
 * @example
 * var size = ioLib.getSize(stream);
 *
 * @returns {number} Returns the size of a stream or string.
 */
exports.getSize = function (val) {
    return bean.getSize(val);
};

/**
 * Returns the mime-type from a name or extension.
 *
 * @example
 * var mimeType = ioLib.getMimeType('myfile.txt');
 *
 * @returns {string} Mime-type of name or extension.
 */
exports.getMimeType = function (name) {
    return bean.getMimeType(name);
};
