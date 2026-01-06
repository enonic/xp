import type {
    App,
    DoubleUnderscore,
    Log,
    Resolve,
    XpRequire,
} from '@enonic-types/core';


declare global {
    /**
     * The globally available app object holds information about the contextual application.
     * @example
     * var nameVersion = app.name + ' v' + app.version;
     *
     * @global
     * @namespace
     */
    const app: App;

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
    const log: Log;

    /**
     * JavaScript to Java bridge functions.
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
    const __: DoubleUnderscore;

    /**
     * This globally available function will load a JavaScript file and return the exports as objects.
     * The function implements parts of the `CommonJS Modules Specification`.
     *
     * @example
     * // Require relative to this
     * var other = require('./other.js');
     *
     * @example
     * // Require absolute
     * var other = require('/path/to/other.js');
     *
     * @example
     * // Require without .js extension
     * var other = require('./other');
     *
     * @param {string} path Path for javascript file (relative or absolute and .js ending is optional).
     * @returns {object} Exports from loaded javascript.
     * @global
     */
    const require: XpRequire;

    /**
     * Resolves a path to another file. Can use relative or absolute path.
     *
     * @example
     * // Resolve relative to this
     * var path = resolve('./other.html');
     *
     * @example
     * // Resolve absolute
     * var path = resolve('/path/to/other.html');
     *
     * @param {string} path Path to resolve.
     * @returns {*} Reference to an object.
     * @global
     */
    const resolve: Resolve;
}

// Making sure the file is a module
export {};
