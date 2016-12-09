var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Rename content by id. Keeps same parent.
var result = repo.push({
    keys: ['a'],
    target: 'otherBranch',
    resolve: true,
    includeChildren: true
});
// END

// BEGIN
// Node created.
var expected = {
    "success": [
        "a",
        "b",
        "c"
    ],
    "failed": [
        {
            "id": "d",
            "reason": "ACCESS_DENIED"
        }
    ],
    "deleted": []
};
// END

assert.assertJsonEquals(expected, result);


