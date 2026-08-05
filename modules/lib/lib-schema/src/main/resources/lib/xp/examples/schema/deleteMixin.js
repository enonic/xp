var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic mixin.

var params = {
    name: 'myapp:mydata'
};

var result = schemaLib.deleteMixin(params);


if (result) {
    log.info('Deleted mixin: ' + params.name);
} else {
    log.info('Mixin deletion failed: ' + params.name);
}

// END


assert.assertTrue(result);

