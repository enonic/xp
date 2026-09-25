var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance*/

// BEGIN
// Set dynamic macro icon.
var result = schemaLib.setMacroIcon({
    key: 'myapp:mymacro',
    data: testInstance.createByteSource('<svg/>'),
    mimeType: 'image/svg+xml'
});

// END


assert.assertEquals('image/svg+xml', result.mimeType);
assert.assertNotNull(result.data);
