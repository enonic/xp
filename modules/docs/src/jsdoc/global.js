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
    version: '',

    /**
     * Application configuration. This configuration is set using files in
     * $XP_HOME/config/<app-name>.cfg.
     *
     * @type Object
     */
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

/**
 * Javascript to Java bridge functions.
 *
 * @example
 * var bean = __.newBean('com.enonic.xp.MyJavaUtils');
 *
 * @example
 * return __.toNativeObject(bean.findArray(arrayName));
 *
 * @global
 * @namespace
 */
var __ = {

    /**
     * Creates a new JavaScript bean that wraps the given Java class and makes it's methods available to be called from JavaScript.
     *
     * @param name Classname for bean to create.
     */
    newBean: function (name) {

    },

    /**
     * Converts JSon to a Java Map structure that can be used as parameters to a Java method on a bean created with <code>newBean</code>.
     *
     * @param value Value to convert.
     */
    toScriptValue: function (value) {

    },

    /**
     * Converts arrays or complex Java objects to JSon.
     *
     * @param value Value to convert.
     */
    toNativeObject: function (value) {

    },

    /**
     * Converts a JavaScript variable that is undefined to a Java <code>null</code> object.
     * If the JavaScript variable is defined, it is returned as is.
     *
     * @param value Value to convert.
     */
    nullOrValue: function (value) {

    },

    /**
     * Doc registerMock.
     *
     * @param name Name of mock.
     * @param value Value to register.
     */
    registerMock: function (name, value) {

    },

    /**
     * Add a disposer that is called when the app is stopped.
     *
     * @param func Function to call.
     */
    disposer: function (func) {

    }
};
