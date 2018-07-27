var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

var result = repo.getActiveVersions({
    key: 'nodeId',
    branches: ['draft', 'master']
});

var expected = {
    "draft": {
        "versionId": "nodeVersionId1",
        "nodeId": "nodeId1",
        "nodePath": "/",
        "timestamp": "1970-01-01T00:16:40Z"
    },
    "master": {
        "versionId": "nodeVersionId2",
        "nodeId": "nodeId2",
        "nodePath": "/",
        "timestamp": "1970-01-01T00:16:40Z"
    }
}

assert.assertJsonEquals(expected, result);
