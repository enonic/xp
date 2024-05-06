var assert = require('/lib/xp/testing');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});


// Update root-permissions
var result1 = repo.setRootPermissions({
    _permissions: [
        {
            'principal': 'role:system.admin',
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
});

log.info('Modified root node, permissions: %s',
    JSON.stringify(result1._permissions, null, 4));
// END

