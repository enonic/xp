var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Creates a repository with default configuration
var result1 = repoLib.create({
    id: 'test-repo'
});

log.info('Repository created with id ' + result1.id);
// END

// BEGIN
// Creates a repository with specific settings
var result2 = repoLib.create({
    id: 'test-repo2',
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
            ],
            'deny': []
        }
    ],
    rootChildOrder: '_ts DESC',
    transient: true,
});

log.info('Repository created with id ' + result2.id);
// END

// BEGIN
// First repository created.
var expected1 = {
    'id': 'test-repo',
    'transient': false,
    'branches': [
        'master'
    ],
    settings: {},
    data: {}
};
// END
assert.assertJsonEquals(expected1, result1);

var expected2 =
    {
        'id': 'test-repo2',
        'transient': true,
        'branches': [
            'master'
        ],
        'settings': {},
        'data': {}
    };
assert.assertJsonEquals(expected2, result2);
