import type {App} from '../core/index';

import {
    expectError,
    expectType,
} from 'tsd';

declare global {
    // Ignore this error in code editor, it's needed when running the tests.
    const app: App;
}

// The reads come first: assigning to app.config['someKey'] below narrows it to string.
expectType<string>(app.name);
expectType<string>(app.version);
expectType<string | undefined>(app.config['someKey']);

expectError(app.name = 'com.enonic.app.name');
expectError(app.version = '1.0.0');
expectError(app.config = {});
expectError(app.config['someKey'] = 'someValue');
