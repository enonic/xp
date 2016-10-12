var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Fetches a node.
var result = nodeLib.get({
    key: 'myId'
});

if (result) {
    log.info('Node "' + result._id + '" found');
} else {
    log.info('Node not found.');
}
// END

// BEGIN
// Fetches nodes.
var results = nodeLib.get({
    keys: ['/myName', 'anotherId']
});

log.info(results.length + ' nodes found.');
// END

// BEGIN
// Node fetched.
var expected = {
    "_id": "myId",
    "_name": "myName",
    "_path": "/myName",
    "_childOrder": "_timestamp DESC",
    "_indexConfig": {
        "default": {
            "decideByType": false,
            "enabled": true,
            "nGram": false,
            "fulltext": false,
            "includeInAllText": false,
            "indexValueProcessors": []
        },
        "configs": [
            {
                "path": "displayName",
                "config": {
                    "decideByType": false,
                    "enabled": true,
                    "nGram": true,
                    "fulltext": true,
                    "includeInAllText": true,
                    "indexValueProcessors": []
                }
            }
        ]
    },
    "_inheritsPermissions": false,
    "_permissions": [
        {
            "principal": "user:system:anonymous",
            "allow": [
                "READ"
            ],
            "deny": []
        },
        {
            "principal": "role:admin",
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
    "_state": "DEFAULT",
    "_nodeType": "default",
    "_versionKey": "versionKey",
    "_timestamp": "2010-10-10T10:10:10.100Z",
    "displayName": "This is brand new node",
    "someData": {
        "cars": [
            "skoda",
            "tesla model x"
        ],
        "likes": "plywood",
        "numberOfUselessGadgets": 123
    }
};
// END

assert.assertJsonEquals(expected, result);
assert.assertJsonEquals(expected, results[0]);
