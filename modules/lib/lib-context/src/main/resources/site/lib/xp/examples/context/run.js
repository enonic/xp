var contextLib = require('/lib/xp/context');
var assert = require('/lib/xp/assert');

// BEGIN
// Define the callback to be executed.
function callback() {
    return 'Hello from context';
}

// Executes a function using different context.
var result = contextLib.run({
    repository: 'system-repo',
    branch: 'master',
    user: {
        login: 'su',
        userStore: 'system'
    },
    principals: ["role:system.admin"]
}, callback);

log.info('Callback says "%s"', result);
// END

assert.assertEquals('Hello from context', result);
