var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Fetch dynamic macro icon.
var result = schemaLib.getMacroIcon({
    key: 'myapp:mymacro'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
