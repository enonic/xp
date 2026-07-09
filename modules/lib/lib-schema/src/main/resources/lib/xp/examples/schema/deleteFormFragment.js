var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic form fragment.

var params = {
    name: 'myapp:myFragment'
};

var result = schemaLib.deleteFormFragment(params);


if (result) {
    log.info('Deleted FormFragment: ' + params.name);
} else {
    log.info('FormFragment deletion failed: ' + params.name);
}

// END


assert.assertTrue(result);

