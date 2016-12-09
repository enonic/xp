var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Push nodes from current branch
var result = repo.push({
    keys: ['a'],
    target: 'otherBranch',
    resolve: false
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


