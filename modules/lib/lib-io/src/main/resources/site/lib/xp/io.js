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
 * Read text from a stream.
 *
 * @example
 * var text = ioLib.readText(stream);
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
 * @example
 * var lines = ioLib.readLines(stream);
 *
 * @param stream Stream to read lines from.
 * @returns {string} Returns lines as an array.
 */
exports.readLines = function (stream) {
    return __.toNativeObject(bean.readLines(stream));
};

/**
 * Process lines from a stream.
 *
 * @example
 * ioLib.processLines(stream, function(line) {
 *   log.info(line);
 * });
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
 * @example
 * var size = ioLib.getSize(stream);
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
 * @example
 * var mimeType = ioLib.getMimeType('myfile.txt');
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
 * @example
 * var stream = ioLib.newStream('my text string');
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
 * @param {string} key Resource key to look up.
 * @returns {Resource} Resource reference.
 */
exports.getResource = function (key) {
    var res = bean.getResource(key);
    return new Resource(res);
};
