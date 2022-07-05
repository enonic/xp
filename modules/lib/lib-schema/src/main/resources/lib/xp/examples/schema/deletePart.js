var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    key: 'myapp:mypart',
    type: 'PART'
};

var result = schemaLib.deleteComponent(params);


if (result) {
    log.info('Deleted part: ' + params.key);
} else {
    log.info('Part deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

