var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic macro.
var result = schemaLib.deleteMacro({
    key: 'myapp:mymacro'
});

if (result) {
    log.info('Deleted macro: myapp:mymacro');
} else {
    log.info('Macro deletion failed: myapp:mymacro');
}

// END


assert.assertTrue(result);
