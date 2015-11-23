/**
 * Loads a new javascript file and return all exports from that file. Follows the require-js specification.
 *
 * @example
 * // Require relative to this
 * var other = resolve('./other.js');
 *
 * @example
 * // Require absolute
 * var other = resolve('/path/to/other.js');
 *
 * @example
 * // Require without .js extension
 * var other = resolve('./other');
 *
 * @param {string} path Path for javascript file (relative or absolute and .js ending is optional).
 * @returns {object} Exports from loaded javascript.
 * @global
 */
function require(path) {
    // Implemented elsewhere
}

/**
 * Resolves a path to another file. Can use relative or absolute path.
 *
 * @example
 * // Resolve relative to this
 * var path = resolve('./other.html');
 *
 * @example
 * // Reslove absolute
 * var path = resolve('/path/to/other.html');
 *
 * @param {string} path Path to resolve.
 * @returns {object} Reference to a path.
 * @global
 */
function resolve(path) {
    // Implemented elsewhere
}

/**
 * Application information.
 *
 * @example
 * var nameVersion = app.name + ' v' + app.version;
 *
 * @namespace
 * @global
 */
var app = {
    /**
     * Name of application.
     *
     * @type string
     */
    name: '',

    /**
     * Application version.
     *
     * @type string
     */
    version: ''
};

/**
 * Logging functions.
 *
 * @example
 * // Log with simple message
 * log.debug('My log message');
 *
 * @example
 * // Log with placeholders
 * log.info('My %s message with %s', 'log', 'placeholders');
 *
 * @example
 * // Log a JSON object
 * log.warning('My JSON: %s', {a: 1});
 *
 * @example
 * // Log JSON object using string
 * log.error('My JSON: %s', JSON.stringify({a: 1}, null, 2));
 *
 * @global
 * @namespace
 */
var log = {

    /**
     * Log debug message.
     *
     * @param {Array} args... logging arguments.
     */
    debug: function (args) {

    },

    /**
     * Log info message.
     *
     * @param {Array} args... logging arguments.
     */
    info: function (args) {

    },

    /**
     * Log warning message.
     *
     * @param {Array} args... logging arguments.
     */
    warning: function (args) {

    },

    /**
     * Log error message.
     *
     * @param {Array} args... logging arguments.
     */
    error: function (args) {

    }
};
