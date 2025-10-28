var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    name: 'myapp:mydata',
    type: 'MIXIN'
};

var result = schemaLib.deleteSchema(params);


if (result) {
    log.info('Deleted x-data: ' + params.key);
} else {
    log.info('X-data deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

