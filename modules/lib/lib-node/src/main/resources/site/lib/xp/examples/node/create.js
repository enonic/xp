var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a content.
var result1 = nodeLib.create({
    _name: "myName",
    displayName: "This is brand new node",
    someData: {
        cars: [
            "skoda", "tesla model X"
        ],
        likes: "plywood",
        numberOfUselessGadgets: 123,
        myGeoPoint: nodeLib.geoPoint(80, -80),
        myInstant: nodeLib.instant("2016-08-01T11:22:00Z"),
        myReference: nodeLib.reference("1234"),
        myLocalDateTime: nodeLib.localDateTime("2016-01-08T10:00:00.000"),
        myLocalDate: nodeLib.localDate("2016-01-08"),
        myLocalTime: nodeLib.localTime("10:00:00.000")
    },
    _indexConfig: {
        default: "minimal",
        configs: {
            path: "displayName",
            config: "fulltext"
        }
    },
    _permissions: [
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
    ]
});

log.info('Node created with id ' + result1._id);
// END

// BEGIN
// Node created.
var expected = {
        "_id": "nodeId",
        "_name": "my-name",
        "_path": "/my-name",
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
    }
    ;
// END

assert.assertJsonEquals(expected, result1);
