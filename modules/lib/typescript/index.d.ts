/// <reference path='global.d.ts' />

declare const app: {
    name: string;
    version: string;
    config: Record<string, string | undefined>;
};

declare const log: {
    debug: (...args: unknown[]) => void;
    info: (...args: unknown[]) => void;
    warning: (...args: unknown[]) => void;
    error: (...args: unknown[]) => void;
};

type NewBean = <T = unknown, Bean extends keyof XpBeans | string = string>(bean: Bean) =>
    Bean extends keyof XpBeans ? XpBeans[Bean] : T;

declare const __: {
    newBean: NewBean;
    toNativeObject: <T = unknown>(beanResult: T) => T;
    toScriptValue: <T = object>(value: T) => T;
    disposer: (callback: (...args: unknown[]) => unknown) => void;
    nullOrValue: <T = object>(value: T) => T | null | undefined;
};

declare type XpRequire = <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : unknown;

declare const require: XpRequire;
