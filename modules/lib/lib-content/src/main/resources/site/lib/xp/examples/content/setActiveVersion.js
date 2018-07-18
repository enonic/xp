var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Return permissions for content by path.
var result = contentLib.setActiveVersion({
    key: '/path/to/mycontent',
    versionId: '90398ddd1b22db08d6a0f9f0d1629a5f4c4fe41d'
});

if (result) {
    log.info('Version set');
} else {
    log.info('Content not found');
}
// END

var expected = true;

assert.assertJsonEquals(expected, result);
