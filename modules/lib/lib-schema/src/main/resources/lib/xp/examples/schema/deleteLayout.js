var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic layout.

var params = {
    key: 'myapp:mylayout'
};

var result = schemaLib.deleteLayout(params);


if (result) {
    log.info('Deleted layout: ' + params.key);
} else {
    log.info('Layout deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

