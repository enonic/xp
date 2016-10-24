var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Diff the given node in the current branch and the targetBranch
var result = nodeLib.diff({
    key: ['a'],
    target: 'otherBranch',
    includeChildren: true
});
// END

// BEGIN
// Node created.
var expected = {
    "diff": [
        {
            "id": "a",
            "status": "NEW"
        },
        {
            "id": "b",
            "status": "MOVED"
        },
        {
            "id": "c",
            "status": "OLDER"
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);


