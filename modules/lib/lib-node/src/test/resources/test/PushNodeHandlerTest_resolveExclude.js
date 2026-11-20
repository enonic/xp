var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

var result = repo.push({
    keys: ['/a'],
    target: 'other-branch',
    resolve: true,
    includeChildren: true,
    exclude: ['/a/b', '/a/c']
});

var expected = {
    'success': [
        'a',
        'd'
    ],
    'failed': [],
};

assert.assertJsonEquals(expected, result);


