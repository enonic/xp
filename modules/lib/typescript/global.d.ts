interface XpLibraries {}

interface XpBeans {}

/**
 The globally available app object holds information about the contextual application.
 */
declare const app: {
    /**
     * The name of the application.
     */
    name: string;
    /**
     * Version of the application.
     */
    version: string;
    /**
     * Values from the application’s configuration file.
     * This can be set using $XP_HOME/config/<app.name>.cfg.
     * Every time the configuration is changed the app is restarted.
     */
    config: Record<string, string | undefined>;
};

/**
 * This globally available log object holds the logging methods.
 */
declare const log: {
    debug: (...args: unknown[]) => void;
    info: (...args: unknown[]) => void;
    warning: (...args: unknown[]) => void;
    error: (...args: unknown[]) => void;
};

declare interface ScriptValue {
    isArray(): boolean;

    isObject(): boolean;

    isValue(): boolean;

    isFunction(): boolean;

    getValue(): unknown;

    getKeys(): string[];

    hasMember(key: string): boolean;

    getMember(key: string): ScriptValue;

    getArray(): ScriptValue[];

    getMap(): Record<string, unknown>;

    getList(): object[];
}

type NewBean = <T = unknown, Bean extends keyof XpBeans | string = string>(bean: Bean) =>
    Bean extends keyof XpBeans ? XpBeans[Bean] : T;

/**
 * The double underscore is available in any server-side JavaScript code and is used for wrapping Java objects in a JavaScript object.
 */
declare const __: {
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
     * Converts a JavaScript variable that is undefined to a Java null object. If the JavaScript variable is defined, it is returned as is.
     * @param value Value to convert
     */
    nullOrValue: <T = object>(value: T) => T | null | undefined;
};

declare type XpRequire = <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : unknown;

/**
 * This globally available function will load a JavaScript file and return the exports as objects.
 * The function implements parts of the `CommonJS Modules Specification`.
 */
declare const require: XpRequire;
