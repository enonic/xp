var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create a namespace.
var result = schemaLib.createNamespace({
    key: 'myapp',
    description: 'My namespace'
});

log.info('Created namespace: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp',
    description: 'My namespace'
}, result);
