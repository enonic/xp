export type IsAny<T> = 0 extends 1 & T ? true : false;

export type IsUnknown<T> = IsAny<T> extends true ? false : unknown extends T ? true : false;

// A resolved type: neither `any` from a broken import nor `unknown` from a missing `XpLibraries` registration.
export type IsTyped<T> = IsAny<T> extends true ? false : IsUnknown<T> extends true ? false : true;

export type Equal<A, B> = (<T>() => T extends A ? 1 : 2) extends (<T>() => T extends B ? 1 : 2) ? true : false;

export type Assert<T extends true> = T;

export type SelfChecks = [
    Assert<Equal<IsAny<any>, true>>,
    Assert<Equal<IsAny<unknown>, false>>,
    Assert<Equal<IsUnknown<unknown>, true>>,
    Assert<Equal<IsUnknown<any>, false>>,
    Assert<Equal<IsTyped<any>, false>>,
    Assert<Equal<IsTyped<unknown>, false>>,
    Assert<Equal<IsTyped<() => void>, true>>,
    Assert<Equal<Equal<string, any>, false>>,
    Assert<Equal<Equal<{a: 1}, {a: 1}>, true>>,
];
