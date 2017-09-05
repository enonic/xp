var nodeLib = require('/lib/xp/node');
var t = require('/lib/xp/testing');

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

t.assertJsonEquals(expected, result);


