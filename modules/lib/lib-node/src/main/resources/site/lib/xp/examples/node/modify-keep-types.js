var nodeLib = require('/lib/xp/node');
var valueLib = require('/lib/xp/value');
var assert = require('/lib/xp/testing');

var TestClass = Java.type('com.enonic.xp.lib.node.BaseNodeHandlerTest');
var stream1 = TestClass.createByteSource('Hello World');


// BEGIN

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// Editor to call for node.
function editor(node) {
    node.myString = 'modifiedValue';
    return node;
}

// Modify node by id
var result = repo.modify({
    key: 'abc',
    editor: editor
});

if (result) {
    log.info('Node modified');
} else {
    log.info('Node not found');
}
// END

// BEGIN
// Node modified.
var expected = {
    "_id": "abc",
    "_name": "myNode",
    "_path": "/myNode",
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
    "_inheritsPermissions": false,
    "_state": "DEFAULT",
    "_nodeType": "default",
    "myString": "modifiedValue",
    "untouchedString": "originalValue",
    "untouchedBoolean": true,
    "untouchedDouble": 2,
    "untouchedLong": 2,
    "untouchedLink": "myLink",
    "untouchedInstant": "2017-01-02T10:00:00Z",
    "untouchedBinaryRef": "abcd",
    "untouchedGeoPoint": "30.0,-30.0",
    "untouchedLocalDate": "2017-03-24",
    "untouchedReference": "myReference"
};
// END

assert.assertJsonEquals(expected, result);
