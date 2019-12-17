var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});
var result = repo.getActiveVersion({
    key: 'nodeId'
});

var expected = {
    'versionId': 'nodeVersionId1',
    'nodeId': 'nodeId1',
    'nodePath': '/',
    'timestamp': '1970-01-01T00:16:40Z',
    'branches': ''
};

assert.assertJsonEquals(expected, result);
