/**
 * Loads a new javascript file and return all exports from that file. Follows the require-js specification.
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
