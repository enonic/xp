var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Sort node
var result = repo.sort({
    key: 'nodeid',
    childOrder: 'field DESC'
});
// END

// BEGIN
// Node sorted
var expected = {
    node: {
        '_id': 'nodeid',
        '_name': 'my-name',
        '_path': '/my-name',
        '_childOrder': 'field DESC',
        '_indexConfig': {
            'default': {
                'decideByType': false,
                'enabled': true,
                'nGram': false,
                'fulltext': false,
                'includeInAllText': false,
                'path': false,
                'indexValueProcessors': [],
                'languages': []
            },
            'configs': [
                {
                    'path': 'displayname',
                    'config': {
                        'decideByType': false,
                        'enabled': true,
                        'nGram': true,
                        'fulltext': true,
                        'includeInAllText': true,
                        'path': false,
                        'indexValueProcessors': [],
                        'languages': []
                    }
                }
            ],
            'allText': {
                'enabled': true,
                'nGram': true,
                'fulltext': true,
                'languages': []
            }
        },
        '_permissions': [
            {
                'principal': 'role:admin',
                'allow': [
                    'READ',
                    'CREATE',
                    'MODIFY',
                    'DELETE',
                    'PUBLISH',
                    'READ_PERMISSIONS',
                    'WRITE_PERMISSIONS'
                ],
                'deny': []
            }
        ],
        '_nodeType': 'default',
        '_versionKey': 'versionkey',
        '_ts': '2010-10-10T10:10:10.100Z',
        'displayName': 'This is brand new node',
        'someData': {
            'cars': [
                'skoda',
                'tesla model x'
            ],
            'likes': 'plywood',
            'numberOfUselessGadgets': 123
        }
    },
    reorderedNodes: []
};
// END

assert.assertJsonEquals(expected, result);

// The reorder anchors are the _orderKey values read off the sibling nodes.
var above = {_orderKey: '3ala4x8dnbc.above-sibling-id'};
var below = {_orderKey: '3ala4xa1kfj.below-sibling-id'};

// BEGIN
// Switch children to manual ordering and drop one child between two of its
// siblings: 'afterOrderKey' is the '_orderKey' of the sibling directly above
// the drop point, 'beforeOrderKey' of the one below it.
var reordered = repo.sort({
    key: 'nodeid',
    childOrder: 'manual',
    reorder: [{
        nodeId: 'child-node-id',
        afterOrderKey: above._orderKey,
        beforeOrderKey: below._orderKey
    }]
});
// END

assert.assertEquals('nodeid', reordered.node._id);
