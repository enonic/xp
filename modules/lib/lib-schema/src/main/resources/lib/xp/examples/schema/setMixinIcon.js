var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance*/

// BEGIN
// Set dynamic mixin icon.
var result = schemaLib.setMixinIcon({
    name: 'myapp:mymixin',
    data: testInstance.createByteSource('<svg/>'),
    mimeType: 'image/svg+xml'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
