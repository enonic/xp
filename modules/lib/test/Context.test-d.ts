import type {
    Context,
    ContextAttributeValue,
} from '../lib-context/src/main/resources/lib/xp/context';

import {
    expectAssignable,
    expectNotAssignable,
} from 'tsd';

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
