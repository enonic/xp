export type EmptyObject = Record<string, never>;

export type AnyObject = Record<string | number | symbol, unknown>;

export type Without<T, U> = { [P in Exclude<keyof T, keyof U>]?: never };

export type XOR<T, U> = T | U extends object ? (Without<T, U> & U) | (Without<U, T> & T) : T | U;

export type Flattened<T> = T extends (Array<infer U> | ReadonlyArray<infer U>) ? U : T;

export type WithRequiredProperty<T, K extends keyof T> = T & { [P in K]-?: T[P] };

export type KeysOfType<O, T> = { [K in keyof O]: O[K] extends T ? K : never }[keyof O];

/**
 * Code suggestions improvement. Using string will lead some code editors to
 * omit the more narrow types, if just the | operator is used.
 */
export type LiteralUnion<T extends U, U = string> = T | (U & Record<never, never>);
