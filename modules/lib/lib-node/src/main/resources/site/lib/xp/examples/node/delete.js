var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Deletes a node.
var result1 = repo.delete({
    key: 'nodeId'
});

log.info(result1.length + ' nodes deleted.');
// END

// BEGIN
// Deletes nodes.
var result2 = repo.delete({
    keys: ['nodeId', '/node2-path', 'anotherNodeId']
});

log.info(result2.length + ' nodes deleted.');
// END

// BEGIN
// Node deleted id.
var expected = ['nodeId', 'aSubNodeId'];
// END

assert.assertJsonEquals(expected, result1);
assert.assertJsonEquals(2, result1.length);
assert.assertJsonEquals(4, result2.length);
