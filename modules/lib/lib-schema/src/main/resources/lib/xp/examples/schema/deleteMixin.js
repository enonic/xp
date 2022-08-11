var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    name: 'myapp:mymixin',
    type: 'MIXIN'
};

var result = schemaLib.deleteSchema(params);


if (result) {
    log.info('Deleted mixin: ' + params.key);
} else {
    log.info('Mixin deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

