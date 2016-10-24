var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Rename content by id. Keeps same parent.
var result = nodeLib.push({
    keys: ['a'],
    target: 'otherBranch'
});
// END

// BEGIN
// Node created.
var expected = {
    "success": [
        "a"
    ],
    "failed": [],
    "deleted": []
};
// END

assert.assertJsonEquals(expected, result);


