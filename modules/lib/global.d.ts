//──────────────────────────────────────────────────────────────────────────────
// Type definitions
// Single source of truth for type definitions that are both global and exported
//──────────────────────────────────────────────────────────────────────────────
type NewBeanDefinition = <T = unknown, Bean extends keyof XpBeans | string = string>(bean: Bean) =>
    Bean extends keyof XpBeans ? XpBeans[Bean] : T;

interface ScriptValueDefinition {
    isArray(): boolean;

    isObject(): boolean;

    isValue(): boolean;

    isFunction(): boolean;

    getValue(): unknown;

    getKeys(): string[];

    hasMember(key: string): boolean;

    getMember(key: string): ScriptValueDefinition;

    getArray(): ScriptValueDefinition[];

    getMap(): Record<string, unknown>;

    getList(): object[];
}

//──────────────────────────────────────────────────────────────────────────────
// Declare global types
//──────────────────────────────────────────────────────────────────────────────
declare global {
    type NewBean = NewBeanDefinition;
    type ScriptValue = ScriptValueDefinition;

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
// Exported types
//──────────────────────────────────────────────────────────────────────────────
export type NewBean = NewBeanDefinition;
export type ScriptValue = ScriptValueDefinition;

export interface App {
    /**
     * The name of the application.
     *
     * @type string
     */
    name: string;
    /**
     * Version of the application.
     *
     * @type string
     */
    version: string;
    /**
     * Values from the application’s configuration file.
     * This can be set using $XP_HOME/config/<app.name>.cfg.
     * Every time the configuration is changed the app is restarted.
     *
     * @type Object
     */
    config: Record<string, string | undefined>;
}

export interface DoubleUnderscore {
    /**
     * Creates a new JavaScript bean that wraps the given Java class and makes its methods available to be called from JavaScript.
     */
    newBean: NewBean;
    /**
     * Converts arrays or complex Java objects to JSON.
     * @param value Value to convert
     */
    toNativeObject: <T = unknown>(value: T) => T;
    /**
     * Converts JSON to a Java Map structure that can be used as parameters to a Java method on a bean created with newBean.
     * @param value Value to convert
     */
    toScriptValue: <T = object>(value: T) => ScriptValue;
    /**
     * Add a disposer that is called when the app is stopped.
     * @param callback Function to call
     */
    disposer: (callback: (...args: unknown[]) => unknown) => void;
    /**
     * Converts a JavaScript variable that is undefined to a Java <code>null</code> object.
     * If the JavaScript variable is defined, it is returned as is.
     * @param value Value to convert
     */
    nullOrValue: <T = object>(value: T) => T | null | undefined;

    /**
     * Doc registerMock.
     *
     * @param name Name of mock.
     * @param value Value to register.
     */
    registerMock: (name: string, value: object) => void
}

export interface Log {
    /**
     * Log debug message.
     *
     * @param {Array} args... logging arguments.
     */
    debug: (...args: unknown[]) => void;

    /**
     * Log info message.
     *
     * @param {Array} args... logging arguments.
     */
    info: (...args: unknown[]) => void;

    /**
     * Log warning message.
     *
     * @param {Array} args... logging arguments.
     */
    warning: (...args: unknown[]) => void;

    /**
     * Log error message.
     *
     * @param {Array} args... logging arguments.
     */
    error: (...args: unknown[]) => void;
}

export interface ResourceKey {
    getApplicationKey(): string;
    getPath(): string;
    getUri(): string;
    isRoot(): boolean;
    getName(): string;
    getExtension(): string;
}

export type Resolve = (path: string) => ResourceKey;

export type XpRequire = <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : unknown;

//──────────────────────────────────────────────────────────────────────────────
// Making sure the file is a module
//──────────────────────────────────────────────────────────────────────────────
export {};
