var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
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
    'success': [
        'a'
    ],
    'failed': [],
};
// END

assert.assertJsonEquals(expected, result);


