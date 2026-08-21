var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch a namespace.
var result = schemaLib.getNamespace({
    key: 'myapp'
});

log.info('Fetched namespace: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp',
    description: 'My namespace'
}, result);
