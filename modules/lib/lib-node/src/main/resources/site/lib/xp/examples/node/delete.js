var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Deletes a node.
var result1 = nodeLib.delete({
    key: 'nodeId'
});

log.info(result1.length + ' nodes deleted.');
// END

// BEGIN
// Deletes nodes.
var result2 = nodeLib.delete({
    keys: ['nodeId', '/aNodePath', 'anotherNodeId', '/another/node/path']
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
