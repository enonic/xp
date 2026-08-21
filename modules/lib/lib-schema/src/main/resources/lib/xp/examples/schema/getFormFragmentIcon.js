var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Fetch dynamic form fragment icon.
var result = schemaLib.getFormFragmentIcon({
    name: 'myapp:myfragment'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
