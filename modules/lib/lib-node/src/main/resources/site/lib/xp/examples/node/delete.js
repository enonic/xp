var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Deletes a node.
var result1 = repo.delete('nodeId');

log.info(result1.length + ' nodes deleted.');
// END

// BEGIN
// Deletes nodes.
var result2 = repo.delete('nodeId', '/node2-path', 'anotherNodeId');

log.info(result2.length + ' nodes deleted.');
// END


// BEGIN
// Deletes nodes.
var nodeIds = ['nodeId', '/node2-path'];
var result3 = repo.delete(nodeIds);

log.info(result3.length + ' nodes deleted.');
// END


assert.assertJsonEquals(2, result1.length);
assert.assertJsonEquals(4, result2.length);
assert.assertJsonEquals(4, result3.length);
