var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete a namespace.
var result = schemaLib.deleteNamespace({
    key: 'myapp'
});

if (result) {
    log.info('Deleted namespace: myapp');
} else {
    log.info('Failed to delete namespace: myapp');
}

// END


assert.assertEquals(true, result);
