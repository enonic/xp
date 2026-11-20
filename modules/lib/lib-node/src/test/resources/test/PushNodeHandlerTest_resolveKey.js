var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

var result = repo.push({
    key: 'a',
    target: 'other-branch',
    resolve: true,
    includeChildren: true
});

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

assert.assertJsonEquals(expected, result);


