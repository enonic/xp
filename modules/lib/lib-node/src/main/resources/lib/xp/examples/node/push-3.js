var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Rename content by id. Keeps same parent.
var result = repo.push({
    keys: ['/a'],
    target: 'otherBranch',
    resolve: true,
    includeChildren: true,
    exclude: ['/a/b', '/a/c']
});
// END

// BEGIN
// Node created.
var expected = {
    'success': [
        'a',
        'd'
    ],
    'failed': [],
};
// END

assert.assertJsonEquals(expected, result);


