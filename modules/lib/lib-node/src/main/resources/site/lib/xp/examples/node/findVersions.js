var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

var result = repo.findVersions({
    key: 'nodeId',
    from: 0,
    size: 20
});

var expected = {
    "total": 40,
    "count": 20,
    "hits": [
        {
            "versionId": "nodeVersionNew",
            "nodeId": "nodeId1",
            "nodePath": "/",
            "timestamp": "1970-01-01T00:16:40Z"
        },
        {
            "versionId": "nodeVersionOld",
            "nodeId": "nodeId1",
            "nodePath": "/",
            "timestamp": "1970-01-01T00:16:40Z"
        }
    ]
};

assert.assertJsonEquals(expected, result);