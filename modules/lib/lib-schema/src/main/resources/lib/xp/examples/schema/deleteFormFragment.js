var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    name: 'myapp:myFragment',
    type: 'FORM_FRAGMENT'
};

var result = schemaLib.deleteSchema(params);


if (result) {
    log.info('Deleted FormFragment: ' + params.key);
} else {
    log.info('FormFragment deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

