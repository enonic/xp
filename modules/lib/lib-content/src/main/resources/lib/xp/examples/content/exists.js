var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Checks if a content exists
var result = contentLib.exists({
    key: '/path/to/mycontent'
});
// END

assert.assertTrue(result);
