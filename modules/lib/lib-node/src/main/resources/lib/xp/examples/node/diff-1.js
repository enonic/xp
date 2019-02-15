var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "com.enonic.cms.default",
    branch: "master"
});

// BEGIN
// Diff the given node in the current branch and the targetBranch
var result = repo.diff({
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


