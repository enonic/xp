var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    key: 'myapp:mypage',
    type: 'PAGE'
};

var result = schemaLib.deleteComponent(params);


if (result) {
    log.info('Deleted page: ' + params.key);
} else {
    log.info('Page deletion failed: ' + params.key);
}

// END


assert.assertTrue(result);

