var contextLib = require('/lib/xp/context');
var assert = require('/lib/xp/testing');

// BEGIN
// Returns the current context.
var result = contextLib.get();
log.info('Context as JSON %s', result);
// END

// BEGIN
// Context returned.
var expected = {
    'authInfo': {
        'principals': [
            'user:system:anonymous',
            'role:system.everyone'
        ]
    },
    'attributes': {}
};
// END

assert.assertJsonEquals(expected, result);
