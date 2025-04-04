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

    node._childOrder = 'updatedOrderField DESC';
    node._indexConfig = {
        default: 'fulltext',
        configs: {
            path: 'displayName',
            config: 'none'
        }
    };

    node.myString = 'modified';
    node.mySet.myGeoPoint = valueLib.geoPoint(0, 0);
    node.myArray = ['modified1', 'modified2', 'modified3'];
    node.myBinaryReference = valueLib.binary('myFile', stream1);

    delete node.toBeRemoved;

    return node;
}

// Update node by id
var result = repo.update({
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
    '_id': 'abc',
    '_name': 'myNode',
    '_path': '/myNode',
    '_childOrder': 'updatedorderfield DESC',
    '_indexConfig': {
        'default': {
            'decideByType': false,
            'enabled': true,
            'nGram': true,
            'fulltext': true,
            'includeInAllText': true,
            'path': false,
            'indexValueProcessors': [],
            'languages': []
        },
        'configs': [
            {
                'path': 'displayName',
                'config': {
                    'decideByType': false,
                    'enabled': false,
                    'nGram': false,
                    'fulltext': false,
                    'includeInAllText': false,
                    'path': false,
                    'indexValueProcessors': [],
                    'languages': []
                }
            }
        ]
    },
    '_permissions': [
        {
            'principal': 'role:system.everyone',
            'allow': [
                'READ'
            ],
            'deny': []
        }
    ],
    '_state': 'DEFAULT',
    '_nodeType': 'default',
    'notChanged': 'originalValue',
    'myString': 'modified',
    'mySet': {
        'myGeoPoint': '0.0,0.0'
    },
    'myArray': [
        'modified1',
        'modified2',
        'modified3'
    ],
    'myBinaryReference': 'myFile'
};
// END

assert.assertJsonEquals(expected, result);
