var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});


// BEGIN
// Fetches a node from an array of keys.

var result3 = repo.get('node1', 'node2');

log.info(result3.length + ' nodes found.');
// END


// BEGIN
// Fetches a node from an array of keys.

var nodeKeys = ['node1', 'node2', 'node3'];
var result1 = repo.get(nodeKeys);

log.info(result1.length + ' nodes found.');
// END


// BEGIN
// Fetches a node from an array of keys.

var nodeKeys1 = ['node1', 'node2', 'node3'];
var nodeKeys2 = ['node4', 'node5', 'node6'];
var result2 = repo.get(nodeKeys1, nodeKeys2);

log.info(result2.length + ' nodes found.');
// END


assert.assertJsonEquals(3, result1.length);
assert.assertJsonEquals(6, result2.length);
assert.assertJsonEquals(2, result3.length);

