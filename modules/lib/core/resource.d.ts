// Compliant with npm module ts-brand
type Brand<
    Base,
    Branding
> = Base & {
  '__type__': Branding
};

export type ByteSource = Brand<object, 'ByteSource'>;

export declare interface Resource {
    getSize(): number;

    getTimestamp(): number;

    getStream(): ByteSource;

    exists(): boolean;
}

export declare interface ResourceKey {
    getApplicationKey(): string;
    getPath(): string;
    getUri(): string;
    isRoot(): boolean;
    getName(): string;
    getExtension(): string;
}
