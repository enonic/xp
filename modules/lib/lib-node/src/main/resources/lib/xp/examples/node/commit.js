var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "draft"
});

// BEGIN
// Commits a node.
var result1 = repo.commit({keys: 'nodeId'});

log.info('Commit created with id ' + result1._id);
// END

// BEGIN
// Commit created.
var expected1 = {
    "id": "aa1f76bf-4bb9-41be-b166-03561c1555b2",
    "message": "",
    "committer": "user:system:anonymous",
    "timestamp": "2019-01-24T15:16:36.260799Z"
};
// END

// BEGIN
// Commits nodes.
var result2 = repo.commit({
    keys: ['nodeId', 'nodeId2'],
    message: 'Commit message'
});

log.info('Commit created with id ' + result2._id);
// END

// BEGIN
// Commit created.
var expected2 = {
    "id": "aa1f76bf-4bb9-41be-b166-03561c1555b2",
    "message": "Commit message",
    "committer": "user:system:anonymous",
    "timestamp": "2019-01-24T15:16:36.260799Z"
};
// END


assert.assertJsonEquals(expected1, result1);
assert.assertJsonEquals(expected2, result2);
