var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});
var result = repo.getActiveVersion({
    key: 'nodeid'
});

var expected = {
    'versionId': 'nodeversionid1',
    'nodeId': 'nodeid1',
    'nodePath': '/',
    'timestamp': '1970-01-01T00:16:40Z'
};

assert.assertJsonEquals(expected, result);
