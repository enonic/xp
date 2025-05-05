var assert = require('/lib/xp/testing');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});


// Apply permissions to a node
var result = repo.applyPermissions({
    key: '/my-node',
    branches: ['draft'],
    permissions: [
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

log.info('Applied permissions: %s',
    JSON.stringify(result, null, 4));
// END

