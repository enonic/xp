var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Delete dynamic macro icon.
var result = schemaLib.deleteMacroIcon({
    key: 'myapp:mymacro'
});

// END


assert.assertTrue(result);
