var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual mixin.

var params = {
    name: 'myapp:mydata',
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

