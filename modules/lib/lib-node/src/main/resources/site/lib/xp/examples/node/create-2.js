var assert = require('/lib/xp/assert');

var TestClass = Java.type('com.enonic.xp.lib.node.BaseNodeHandlerTest');
var byteSource1 = TestClass.createByteSource('Hello World');
var byteSource2 = TestClass.createByteSource('Hello World2');

// BEGIN
var nodeLib = require('/lib/xp/node');
var valueLib = require('/lib/xp/value');

// Connect to repo
var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// Create node.
var result1 = repo.create({
    _name: "myName",
    displayName: "This is brand new node",
    someData: {
        cars: [
            "skoda", "tesla model X"
        ],
        likes: "plywood",
        numberOfUselessGadgets: 123,
        myGeoPoint: valueLib.geoPoint(80, -80),
        myGeoPoint2: valueLib.geoPointString("80,-30"),
        myInstant: valueLib.instant("2016-08-01T11:22:00Z"),
        myReference: valueLib.reference("1234"),
        myLocalDateTime: valueLib.localDateTime("2016-01-08T10:00:00.000"),
        myLocalDate: valueLib.localDate("2016-01-08"),
        myLocalTime: valueLib.localTime("10:00:00.000"),
        myBinaryReference: valueLib.binary('myFile', byteSource1),
        myBinaryReference2: valueLib.binary('myFile2', byteSource2)
    },
    _indexConfig: {
        default: "byType",
        configs: [
            {
                path: "displayName",
                config: "fulltext"
            },
            {
                path: "someData.cars",
                config: "minimal"
            }]
    },
    _permissions: [
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
        "_attachedBinaries": [],
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
