export declare interface NestedRecord {
	[name: PropertyKey]: NestedRecord | unknown
}

/**
 * Makes it possible to see the full type on hover in Visual Studio Code. 
 */
export declare type Prettify<T> = {
    [K in keyof T]: T[K];
} & {};
