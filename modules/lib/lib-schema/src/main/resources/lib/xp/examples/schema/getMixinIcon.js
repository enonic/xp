var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Fetch dynamic mixin icon.
var result = schemaLib.getMixinIcon({
    name: 'myapp:mymixin'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
