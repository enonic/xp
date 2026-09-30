var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance*/

// BEGIN
// Set dynamic part icon.
var result = schemaLib.setPartIcon({
    key: 'myapp:mypart',
    data: testInstance.createByteSource('<svg/>'),
    mimeType: 'image/svg+xml'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
