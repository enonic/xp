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
    resolve: {
        includeChildren: true,
        exclude: ['b', 'c']
    }
});
// END

// BEGIN
// Node created.
var expected = {
    "success": [
        "a",
        "d"
    ],
    "failed": [],
    "deleted": []
};
// END

assert.assertJsonEquals(expected, result);


