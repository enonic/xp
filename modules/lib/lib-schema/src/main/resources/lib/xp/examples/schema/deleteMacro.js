var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete virtual macro.
var result = schemaLib.deleteMacro({
    key: 'myapp:mymacro'
});

log.info('Deleted macro: ' + result);

// END


assert.assertEquals(true, result);
