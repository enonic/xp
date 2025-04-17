var nodeLib = require('/lib/xp/node');
var valueLib = require('/lib/xp/value');
var assert = require('/lib/xp/testing');

var TestClass = Java.type('com.enonic.xp.lib.node.BaseNodeHandlerTest');
var stream1 = TestClass.createByteSource('Hello World');


// BEGIN

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// Editor to call for node.
function editor(node) {
    node.myString = 'modifiedValue';
    return node;
}

// Update node by id
var result = repo.update({
    key: 'abc',
    editor: editor
});

if (result) {
    log.info('Node updated');
} else {
    log.info('Node not found');
}
// END

// BEGIN
// Node updated.
var expected = {
    '_id': 'abc',
    '_name': 'myNode',
    '_path': '/myNode',
    '_childOrder': '_ts DESC',
    '_indexConfig': {
        'default': {
            'decideByType': true,
            'enabled': true,
            'nGram': false,
            'fulltext': false,
            'includeInAllText': false,
            'path': false,
            'indexValueProcessors': [],
            'languages': []
        },
        'configs': []
    },
    '_state': 'DEFAULT',
    '_nodeType': 'default',
    'myString': 'modifiedValue',
    'untouchedString': 'originalValue',
    'untouchedBoolean': true,
    'untouchedDouble': 2,
    'untouchedLong': 2,
    'untouchedLink': 'myLink',
    'untouchedInstant': '2017-01-02T10:00:00Z',
    'untouchedBinaryRef': 'abcd',
    'untouchedGeoPoint': '30.0,-30.0',
    'untouchedLocalDate': '2017-03-24',
    'untouchedReference': 'myReference'
};
// END

assert.assertJsonEquals(expected, result);
