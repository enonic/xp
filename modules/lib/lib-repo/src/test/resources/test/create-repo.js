var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/testing');

var result = repoLib.create({
    id: 'test-repo',
    rootPermissions: [
        {
            'principal': 'role:admin',
            'allow': [
                'READ',
                'CREATE',
                'MODIFY',
                'DELETE',
                'PUBLISH',
                'READ_PERMISSIONS',
                'WRITE_PERMISSIONS'
            ]
        }
    ],
    rootChildOrder: '_ts DESC'
});

assert.assertJsonEquals({
    'id': 'test-repo',
    'transient': false,
    'branches': [
        'master'
    ],
    'settings': {},
    'data': {}
}, result);
