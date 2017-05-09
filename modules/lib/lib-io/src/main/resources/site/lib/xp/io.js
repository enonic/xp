/**
 * IO related functions.
 *
 * @example
 * var ioLib = require('/lib/xp/io');
 *
 * @module io
 */

var bean = __.newBean('com.enonic.xp.lib.io.IOHandlerBean');

/**
 * Read text from a stream.
 *
 * @example-ref examples/io/readText.js
 *
 * @param stream Stream to read text from.
 * @returns {string} Returns the text read from stream or string.
 */
exports.readText = function (stream) {
    return bean.readText(stream);
};

/**
 * Read lines from a stream.
 *
 * @example-ref examples/io/readLines.js
 *
 * @param stream Stream to read lines from.
 * @returns {string[]} Returns lines as an array.
 */
exports.readLines = function (stream) {
    return __.toNativeObject(bean.readLines(stream));
};

/**
 * Process lines from a stream.
 *
 * @example-ref examples/io/processLines.js
 *
 * @param stream Stream to read lines from.
 * @param {function} func Callback function to be called for each line.
 */
exports.processLines = function (stream, func) {
    return bean.processLines(stream, func);
};

/**
 * Returns the size of a stream.
 *
 * @example-ref examples/io/getSize.js
 *
 * @param stream Stream to get size of.
 * @returns {number} Returns the size of a stream.
 */
exports.getSize = function (stream) {
    return bean.getSize(stream);
};

/**
 * Returns the mime-type from a name or extension.
 *
 * @example-ref examples/io/getMimeType.js
 *
 * @param {string} name Name of file or extension.
 * @returns {string} Mime-type of name or extension.
 */
exports.getMimeType = function (name) {
    return bean.getMimeType(name);
};

/**
 * Returns a new stream from a string.
 *
 * @example-ref examples/io/newStream.js
 *
 * @param {string} text String to create a stream of.
 * @returns {*} A new stream.
 */
exports.newStream = function (text) {
    return bean.newStream(text);
};

/**
 * Looks up a resource.
 *
 * @param {*} native Native resource object.
 * @constructor
 * @private
 */
function Resource(native) {
    this.res = native;
}

/**
 * Returns the resource stream.
 *
 * @returns Stream of resource.
 */
Resource.prototype.getStream = function () {
    return this.res.getBytes();
};

/**
 * Returns the resource size.
 *
 * @returns {number} Size of resource in bytes.
 */
Resource.prototype.getSize = function () {
    return this.res.getSize();
};

/**
 * Returns true if the resource exists.
 *
 * @returns {boolean} True if resource exists.
 */
Resource.prototype.exists = function () {
    return this.res.exists();
};

/**
 * Looks up a resource.
 *
 * @example-ref examples/io/getResource.js
 *
 * @param {string} key Resource key to look up.
 * @returns {Resource} Resource reference.
 */
exports.getResource = function (key) {
    var res = bean.getResource(key);
    return new Resource(res);
};
