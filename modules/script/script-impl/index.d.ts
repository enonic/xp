/// <reference path="global.d.ts" />

declare const app: {
    name: string;
    version: string;
    config: Record<string, string>;
};

declare const log: {
    debug: (...args: unknown[]) => void;
    info: (...args: unknown[]) => void;
    warning: (...args: unknown[]) => void;
    error: (...args: unknown[]) => void;
};

declare const __: {
    newBean: <T = unknown>(bean: string) => T;
    toNativeObject: <T = unknown>(beanResult: T) => T;
    toScriptValue: <T = Object>(value: T) => T;
    disposer: (callback: (...args: unknown[]) => unknown) => void;
};

declare const require: <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : any;

declare const __non_webpack_require__: <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : any;
