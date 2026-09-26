import type {
    Context,
    ContextAttributeValue,
    ContextParams,
    CustomAttributeValue,
} from '../lib-context/src/main/resources/lib/xp/context';

import {
    expectAssignable,
    expectError,
    expectNotAssignable,
    expectType,
} from 'tsd';

declare const contextLib: typeof import('../lib-context/src/main/resources/lib/xp/context');
const {get, setCustomLocalAttribute} = contextLib;

// Scenario: get() returns whatever setCustomLocalAttribute stored, keyed with the custom. prefix.
// Values round trip in full - scalars, arrays and nested objects alike.
const contextWithCustomAttributes = {
    branch: 'draft',
    repository: 'com.enonic.cms.default',
    attributes: {
        'custom.myScalar': 42,
        'custom.myList': ['a', 'b'],
        'custom.myObject': {
            a: 1,
            b: 'text',
            c: true,
            d: ['x', 2, false],
            e: {nested: ['deep']},
        },
    },
};
expectAssignable<Context>(contextWithCustomAttributes);

expectAssignable<ContextAttributeValue>('text');
expectAssignable<ContextAttributeValue>(42);
expectAssignable<ContextAttributeValue>(true);
expectAssignable<ContextAttributeValue>(['a', 'b']);
expectAssignable<ContextAttributeValue>({nested: ['deep']});

// setCustomLocalAttribute rejects anything that is not JSON-like
expectNotAssignable<ContextAttributeValue>(() => 'a function');
expectNotAssignable<ContextAttributeValue>(new Date());

// Scenario: attributes read from one context can be carried into another. One type for both
// directions keeps this assignable - see the round-trip note on ContextAttributes.
const carriedForward: ContextParams = {
    branch: 'master',
    attributes: contextWithCustomAttributes.attributes,
};
expectAssignable<ContextParams>(carriedForward);

// Scenario: the developer names a type and uses it on both ends of setCustomLocalAttribute/get.
// Unchecked at runtime, but it constrains what is written and types what is read.
interface MyData {
    label: string;
    count?: number;
    tags: string[];
    nested: {flag: boolean};
}

// Interfaces are accepted, even though they have no implicit index signature
setCustomLocalAttribute<MyData>('my-data', {label: 'a', tags: ['x'], nested: {flag: true}});
setCustomLocalAttribute<MyData>('my-data', null);
setCustomLocalAttribute<MyData>('my-data');
expectError(setCustomLocalAttribute<MyData>('my-data', {label: 'a'}));
expectError(setCustomLocalAttribute<MyData>('my-data', {label: 1, tags: [], nested: {flag: true}}));

// Without a type argument any JSON-like value is accepted
setCustomLocalAttribute('scalar', 42);
setCustomLocalAttribute('list', ['a', 1, true]);
setCustomLocalAttribute('object', {a: {b: ['c']}});

// Non-JSON values are rejected, with or without a type argument
expectError(setCustomLocalAttribute('fn', () => 'a function'));
expectError(setCustomLocalAttribute('date', new Date()));
expectError(setCustomLocalAttribute('nestedDate', {when: new Date()}));
expectError(setCustomLocalAttribute<Date>('date', new Date()));

// The read side is typed, but may be unset
const typedContext = get<{'custom.my-data': MyData}>();
const myData = typedContext.attributes['custom.my-data'];
expectAssignable<MyData | undefined>(myData);
expectError<MyData>(typedContext.attributes['custom.my-data']);
if (myData) {
    expectType<string>(myData.label);
    expectType<boolean>(myData.nested.flag);
}

// Other attributes stay readable alongside the typed ones
expectAssignable<ContextAttributeValue | undefined>(typedContext.attributes['other']);

// Without a type argument get() returns the plain Context
expectType<Context>(get());

// CustomAttributeValue without a type argument still accepts any JSON-like value
expectAssignable<CustomAttributeValue>('text');
expectAssignable<CustomAttributeValue>(['a', 1, true]);
expectAssignable<CustomAttributeValue>({nested: ['deep']});
expectNotAssignable<CustomAttributeValue>(() => 'a function');
expectNotAssignable<CustomAttributeValue>(new Date());

// Readonly arrays and `as const` data are JSON-like too
interface ReadonlyData {
    tags: readonly string[];
    nested: {ids: readonly number[]};
}
declare const readonlyData: ReadonlyData;
setCustomLocalAttribute<ReadonlyData>('readonly-data', readonlyData);
setCustomLocalAttribute('readonly-data', readonlyData);
setCustomLocalAttribute('const-data', {tags: ['a', 'b'], nested: {ids: [1, 2]}} as const);
expectError(setCustomLocalAttribute('readonly-dates', [new Date()] as const));
