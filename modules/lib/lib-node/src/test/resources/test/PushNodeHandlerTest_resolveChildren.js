var nodeLib = require('/lib/xp/node');
var t = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

var result = repo.push({
    keys: ['a'],
    target: 'other-branch',
    resolve: true,
    includeChildren: true
});

var expected = {
    'success': [
        'a',
        'b',
        'c'
    ],
    'failed': [
        {
            'id': 'd',
            'reason': 'ACCESS_DENIED'
        }
    ],
};

t.assertJsonEquals(expected, result);


