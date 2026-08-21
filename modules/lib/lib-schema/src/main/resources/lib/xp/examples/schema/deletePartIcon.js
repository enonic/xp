var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Delete dynamic part icon.
var result = schemaLib.deletePartIcon({
    key: 'myapp:mypart'
});

// END


assert.assertTrue(result);
