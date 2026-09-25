var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance*/

// BEGIN
// Set dynamic form fragment icon.
var result = schemaLib.setFormFragmentIcon({
    name: 'myapp:myfragment',
    data: testInstance.createByteSource('<svg/>'),
    mimeType: 'image/svg+xml'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
