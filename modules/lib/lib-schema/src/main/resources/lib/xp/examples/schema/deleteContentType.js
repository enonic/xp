var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    name: 'myapp:mytype',
    type: 'CONTENT_TYPE'
};

var result = schemaLib.deleteSchema(params);


if (result) {
    log.info('Deleted content type: ' + params.key);
} else {
    log.info('Content type deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

