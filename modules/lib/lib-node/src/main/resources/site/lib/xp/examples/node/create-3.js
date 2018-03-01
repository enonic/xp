var assert = require('/lib/xp/testing');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// Create node.
var result1 = repo.create({
    _name: "myName",
    displayName: "Child node inheriting permissions",
    _parentPath: "/parent",
    _inheritsPermissions: true
});

// END

// BEGIN
// Node created.
var expected = {
        "_id": "b186d24f-ac38-42ca-a6db-1c1bda6c6c26",
        "_name": "myName",
        "_path": "/parent/myName",
        "_childOrder": "_timestamp DESC",
        "_indexConfig": {
            "default": {
                "decideByType": true,
                "enabled": true,
                "nGram": false,
                "fulltext": false,
                "includeInAllText": false,
                "path": false,
                "indexValueProcessors": []
            },
            "configs": []
        },
        "_inheritsPermissions": true,
        "_permissions": [
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
            },
            {
                "principal": "user:system:user1",
                "allow": [
                    "READ",
                    "CREATE",
                    "MODIFY",
                    "DELETE"
                ],
                "deny": []
            },
            {
                "principal": "role:system.everyone",
                "allow": [
                    "READ"
                ],
                "deny": []
            }
        ],
        "_state": "DEFAULT",
        "_nodeType": "default",
        "displayName": "Child node inheriting permissions"
    }
;
// END

assert.assertJsonEquals(expected, result1);
