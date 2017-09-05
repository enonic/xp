var assert = require('/lib/xp/testing');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});


// Update root-permissions
var result1 = repo.setRootPermissions({
    _permissions: [
        {
            "principal": "role:system.admin",
            "allow": [
                "READ",
                "CREATE",
                "MODIFY",
                "DELETE",
                "PUBLISH",
                "READ_PERMISSIONS",
                "WRITE_PERMISSIONS"
            ],
            "deny": []
        }
    ],
    _inheritsPermissions: true
});

log.info("Modified root node with inheritsPermissions = [%s], permissions: %s", result1._inheritsPermissions,
    JSON.stringify(result1._permissions, null, 4));
// END

