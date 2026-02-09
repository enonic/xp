var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

var result = repo.findVersions({
    key: 'nodeId',
    count: 2
});

var expected = {
    'total': 40,
    'count': 2,
    'hits': [
        {
            'versionId': 'nodeVersionNew',
            'nodeId': 'nodeId1',
            'nodePath': '/',
            'timestamp': '1970-01-01T00:16:40Z'
        },
        {
            'versionId': 'nodeVersionOld',
            'nodeId': 'nodeId1',
            'nodePath': '/',
            'timestamp': '1970-01-01T00:08:20Z'
        }
    ]
};

assert.assertJsonEquals(expected, result);
