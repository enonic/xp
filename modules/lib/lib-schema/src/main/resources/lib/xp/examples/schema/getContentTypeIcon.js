var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Fetch dynamic content type icon.
var result = schemaLib.getContentTypeIcon({
    name: 'myapp:mytype'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
