var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'draft'
});

var expectedJson = {
    "_id": "nodeid-copy",
    "_name": "duplicated-node",
    "_path": "/duplicated-node",
    "_childOrder": "_ts DESC",
    "_indexConfig": {
        "default": {
            "decideByType": true,
            "enabled": true,
            "nGram": false,
            "fulltext": false,
            "includeInAllText": false,
            "path": false,
            "indexValueProcessors": [],
            "languages": []
        }, "configs": [],
        "allText": {
            "enabled": true,
            "nGram": true,
            "fulltext": true,
            "languages": []
        }
    },
    "_permissions": [{
        "principal": "role:system.admin",
        "allow": ["READ", "CREATE", "MODIFY", "DELETE", "PUBLISH", "READ_PERMISSIONS", "WRITE_PERMISSIONS"],
        "deny": []
    }],
    "_nodeType": "default",
    "data": {
        "prop1": "Value 1",
        "extraProp": "extraPropValue"
    }
};

// BEGIN
// Duplicates node
var result = repo.duplicate({
    nodeId: 'nodeId',
    name: 'duplicated-node',
    includeChildren: false,
    dataProcessor: function (data) {
        data.data.extraProp = 'extraPropValue';
        return data;
    },
    parent: '/',
    refresh: 'SEARCH'
});
// END

assert.assertJsonEquals(expectedJson, result);

