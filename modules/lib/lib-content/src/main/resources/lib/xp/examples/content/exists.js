var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Deletes a content by path.
var result = contentLib.exist({
    key: '/path/to/mycontent'
});

assert.assertTrue(result);
