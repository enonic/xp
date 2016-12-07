var contextLib = require('/lib/xp/context');
var assert = require('/lib/xp/assert');

// BEGIN
// Define the callback to be executed.
function callback() {
    return 'Hello from context';
}

// Executes a function using different context.
var result = contextLib.run({
    branch: 'draft',
    user: {
        login: 'su',
        userStore: 'system'
    },
    principals: ["role:system.admin"],
    attributes: {
        'ignorePublishTimes': true
    }
}, callback);

log.info('Callback says "%s"', result);
// END

assert.assertEquals('Hello from context', result);
