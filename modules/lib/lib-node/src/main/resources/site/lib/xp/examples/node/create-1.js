var assert = require('/lib/xp/assert');

var TestClass = Java.type('com.enonic.xp.lib.node.BaseNodeHandlerTest');
var byteSource1 = TestClass.createByteSource('Hello World');
var byteSource2 = TestClass.createByteSource('Hello World2');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// Create node.
var result1 = repo.create({
    likes: "plywood",
    numberOfUselessGadgets: 123
});

log.info('Node created with id ' + result1._id);
// END

// BEGIN
// Node created.
var expected = {
    "_id": "a-random-node-id",
    "_childOrder": "_timestamp DESC",
    "_indexConfig": {
        "default": {
            "decideByType": true,
            "enabled": true,
            "nGram": false,
            "fulltext": false,
            "includeInAllText": false,
            "indexValueProcessors": []
        },
        "configs": []
    },
    "_inheritsPermissions": false,
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
        }
    ],
    "_attachedBinaries": [],
    "_state": "DEFAULT",
    "_nodeType": "default",
    "likes": "plywood",
    "numberOfUselessGadgets": 123
};
// END

assert.assertJsonEquals(expected, result1);
