import type { ResourceKey } from './resource';

export declare interface App {
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

export declare interface Log {
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

export declare interface ScriptValue {
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

export declare type NewBean = <T = unknown, Bean extends keyof XpBeans | string = string>(bean: Bean) =>
    Bean extends keyof XpBeans ? XpBeans[Bean] : T;

export declare interface DoubleUnderscore {
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

export declare type XpRequire = <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : unknown;

export declare type Resolve = (path: string) => ResourceKey;