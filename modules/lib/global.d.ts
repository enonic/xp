import type {
    App,
    DoubleUnderscore,
    Log,
    NewBean as NewBeanType,
    Resolve,
    ScriptValue as ScriptValueInterface,
    XpRequire,
} from '@enonic-types/core';


//──────────────────────────────────────────────────────────────────────────────
// Declare global types
//──────────────────────────────────────────────────────────────────────────────
declare global {
    // TODO: Remove these two? They are now exported by @enonic-types/core and
    // shouldn't be polluting the global namespace anymore.
    type NewBean = NewBeanType;
    type ScriptValue = ScriptValueInterface;

    /**
     * The globally available app object holds information about the contextual application.
     * @example
     * var nameVersion = app.name + ' v' + app.version;
     *
     * @global
     * @namespace
     */
    declare const app: App;

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
    declare const log: Log;

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
    declare const __: DoubleUnderscore;

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
    declare const require: XpRequire;

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
    declare const resolve: Resolve;
}

//──────────────────────────────────────────────────────────────────────────────
// Making sure the file is a module
//──────────────────────────────────────────────────────────────────────────────
export {};
