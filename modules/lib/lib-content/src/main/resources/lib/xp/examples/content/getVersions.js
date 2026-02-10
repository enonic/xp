var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Fetch first page
var result = contentLib.getVersions({
    key: 'contentId',
    count: 2
});

log.info('Total versions: %s', result.total);

// Fetch next page using cursor
if (result.cursor) {
    var nextPage = contentLib.getVersions({
        key: 'contentId',
        count: 2,
        cursor: result.cursor
    });
}
// END

var expected = {
    'total': 5,
    'count': 2,
    'cursor': 'eyJ0cyI6MTcwNDA2NzIwMDAwMCwiaWQiOiJ2ZXJzaW9uMiJ9',
    'hits': [
        {
            'versionId': 'version1',
            'contentId': 'contentId',
            'path': '/my-content',
            'timestamp': '2024-01-01T00:00:00Z',
            'actions': [
                {
                    'operation': 'publish',
                    'user': 'user:system:admin',
                    'opTime': '2024-01-01T00:00:00Z'
                }
            ]
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
