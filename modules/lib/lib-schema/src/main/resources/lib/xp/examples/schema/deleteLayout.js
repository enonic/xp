var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    key: 'myapp:mylayout',
    type: 'LAYOUT'
};

var result = schemaLib.deleteComponent(params);


if (result) {
    log.info('Deleted layout: ' + params.key);
} else {
    log.info('Layout deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

