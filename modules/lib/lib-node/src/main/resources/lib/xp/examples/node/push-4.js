var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Rename content by id. Keeps same parent.
var result = repo.push({
    key: 'a',
    target: 'otherBranch',
    resolve: true,
    includeChildren: true
});
// END

// BEGIN
// Node created.
var expected = {
    'success': [
        'a',
        'b'
    ],
    'failed': [
        {
            'id': 'c',
            'reason': 'ACCESS_DENIED'
        }
    ],
};
// END

assert.assertJsonEquals(expected, result);


