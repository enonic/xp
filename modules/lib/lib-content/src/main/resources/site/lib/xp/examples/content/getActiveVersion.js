var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Return permissions for content by path.
var result = contentLib.getActiveVersion({
    key: '/path/to/mycontent'
});

log.info('Active content version result: ' + JSON.stringify(result, null, 2));
// END

// BEGIN
// Versions result returned.
var expected = {
    "versionId": "1b5c7c8dc0db8a99287b288d965ac4002b22a560",
    "displayName": "My content",
    "modifiedTime": "2018-06-18T00:00:00Z",
    "modifier": "user:system:su"

};
// END

assert.assertJsonEquals(expected, result);
