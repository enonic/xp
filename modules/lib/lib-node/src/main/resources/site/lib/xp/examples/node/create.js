var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a content.
var result1 = nodeLib.create({
    a: 1,
    b: 2,
    c: ['1', '2'],
    d: {
        e: {
            f: 3.6,
            g: true
        }
    }
});

log.info('Node created with id ' + result1._id);
// END

// BEGIN
// Node created.
var expected = {
        "_id": "123456",
        "_name": "myNode",
        "_path": "/myNode",
        "_childOrder": "_timestamp DESC",
        "_indexConfig": {
            "analyzer": "myAnalyzer",
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
                },
                {
                    "path": "myHtmlField",
                    "config": {
                        "decideByType": false,
                        "enabled": true,
                        "nGram": false,
                        "fulltext": false,
                        "includeInAllText": true,
                        "indexValueProcessors": [
                            "myProcessor"
                        ]
                    }
                },
                {
                    "path": "type",
                    "config": {
                        "decideByType": false,
                        "enabled": false,
                        "nGram": false,
                        "fulltext": false,
                        "includeInAllText": false,
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
            },
            {
                "principal": "role:everyone",
                "allow": [
                    "READ"
                ],
                "deny": []
            },
            {
                "principal": "role:authenticated",
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
        "_nodeType": "myNodeType",
        "_versionKey": "versionId",
        "_manualOrderValue": 0,
        "_timestamp": "2010-10-10T10:10:10.100Z",
        "myHtmlField": "<h1>Html here</h1>",
        "displayName": "House1",
        "type": "com.enonic.app.features:house",
        "owner": "user:system:su",
        "modifiedTime": "2015-10-05T12:11:01.272Z"
    }
    ;
// END

assert.assertJsonEquals(expected, result1);
