var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Update a namespace.
var result = schemaLib.updateNamespace({
    key: 'myapp',
    description: 'My updated namespace'
});

log.info('Updated namespace: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp',
    description: 'My updated namespace'
}, result);
