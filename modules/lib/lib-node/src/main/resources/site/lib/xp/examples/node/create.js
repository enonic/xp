var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a content.
var result1 = nodeLib.create({
    name: 'myNode',
    parentPath: '/',
    data: {
        a: 1,
        b: 2,
        c: ['1', '2'],
        d: {
            e: {
                f: 3.6,
                g: true
            }
        }
    },
    manualOrderValue: 0,
    childOrder: "name _asc"
});

log.info('Node created with id ' + result1._id);
// END

// BEGIN
// Node created.
var expected = {
        "id": "123456",
        "name": "myNode",
        "path": "/myNode",
        "attachedBinaries": [
            {
                "binaryReference": "myRef",
                "blobKey": "abc"
            },
            {
                "binaryReference": "myRef2",
                "blobKey": "def"
            }
        ],
        "childOrder": "_timestamp DESC",
        "data": {
            "myHtmlField": "<h1>Html here</h1>",
            "displayName": "House1",
            "type": "com.enonic.app.features:house",
            "owner": "user:system:su",
            "modifiedTime": "2015-10-05T12:11:01.272Z"
        }
        ,
        "indexConfig": {
            "analyzer": "myAnalyzer",
            "patternIndexConfigs": [
                {
                    "path": "displayName",
                    "indexConfig": {
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
                    "indexConfig": {
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
                    "indexConfig": {
                        "decideByType": false,
                        "enabled": false,
                        "nGram": false,
                        "fulltext": false,
                        "includeInAllText": false,
                        "indexValueProcessors": []
                    }
                }
            ]
        }
        ,
        "inheritsPermissions": false,
        "permissions": [
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
        "nodeState": "DEFAULT",
        "nodeType": "myNodeType",
        "nodeVersionId": "versionId",
        "manualOrderValue": 0,
        "timestamp": "2010-10-10T10:10:10.100Z"
    }
    ;
// END

assert.assertJsonEquals(expected, result1);
