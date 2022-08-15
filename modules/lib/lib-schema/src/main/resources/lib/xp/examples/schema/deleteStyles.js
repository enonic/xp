var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual part.

var params = {
    application: 'myapp'
};

var result = schemaLib.deleteStyles(params);


if (result) {
    log.info('Deleted styles: ' + params.application);
} else {
    log.info('Styles deletion failed: ' + params.application);
}

// END


assert.assertTrue(result);

