var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
var result = contentLib.getVersions({
    key: 'contentId',
    count: 2
});

log.info('Total versions: %s', result.total);
// END

var expected = {
    'total': 2,
    'count': 2,
    'hits': [
        {
            'versionId': 'version1',
            'contentId': 'contentId',
            'path': '/my-content',
            'timestamp': '2024-01-01T00:00:00Z'
        },
        {
            'versionId': 'version2',
            'contentId': 'contentId',
            'path': '/my-content',
            'timestamp': '2023-12-01T00:00:00Z'
        }
    ]
};

assert.assertJsonEquals(expected, result);
