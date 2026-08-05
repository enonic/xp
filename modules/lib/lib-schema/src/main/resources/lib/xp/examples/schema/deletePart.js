var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic part.

var params = {
    key: 'myapp:mypart'
};

var result = schemaLib.deletePart(params);


if (result) {
    log.info('Deleted part: ' + params.key);
} else {
    log.info('Part deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

