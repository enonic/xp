var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic content type.

var params = {
    name: 'myapp:mytype'
};

var result = schemaLib.deleteContentType(params);


if (result) {
    log.info('Deleted content type: ' + params.name);
} else {
    log.info('Content type deletion failed: ' + params.name);
}

// END


assert.assertTrue(result);

