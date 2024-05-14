import type { Prettify } from './util';

export declare interface Resource {
    getSize(): number;

    getTimestamp(): number;

    getStream(): ByteSource;

    exists(): boolean;
}

export declare interface ResourceKeyInterface {
    getApplicationKey(): string;
    getPath(): string;
    getUri(): string;
    isRoot(): boolean;
    getName(): string;
    getExtension(): string;
}

export declare type ResourceKey = Prettify<ResourceKeyInterface>;