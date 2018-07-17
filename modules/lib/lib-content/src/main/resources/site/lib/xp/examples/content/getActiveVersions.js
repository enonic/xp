var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Return permissions for content by path.
var result = contentLib.getActiveVersions({
    key: '/path/to/mycontent',
    branches: ['draft', 'master']
});

log.info('Active content versions result: ' + result);
// END

// BEGIN
// Versions result returned.
var expected = {
    "draft": {
        "versionId": "1b5c7c8dc0db8a99287b288d965ac4002b22a560",
        "displayName": "My content",
        "modifiedTime": "2018-06-18T00:00:00Z",
        "modifier": "user:system:su"
    },
    "master": {
        "versionId": "90398ddd1b22db08d6a0f9f0d1629a5f4c4fe41d",
        "displayName": "My content",
        "modifiedTime": "2018-06-15T00:00:00Z",
        "modifier": "user:system:su"
    }
};
// END

assert.assertJsonEquals(expected, result);
