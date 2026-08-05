var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic CMS descriptor.

var params = {
    application: 'myapp'
};

var result = schemaLib.deleteCms(params);


if (result) {
    log.info('Deleted CMS descriptor: ' + params.application);
} else {
    log.info('CMS descriptor deletion failed: ' + params.application);
}

// END


assert.assertTrue(result);
