var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance*/

// BEGIN
// Set dynamic content type icon.
var result = schemaLib.setContentTypeIcon({
    name: 'myapp:mytype',
    data: testInstance.createByteSource('<svg/>'),
    mimeType: 'image/svg+xml'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
