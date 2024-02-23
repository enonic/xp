var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');


var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Fetches a node.
var result1 = repo.get('nodeId');

if (result1) {
    log.info('Node "' + result1._id + '" found');
} else {
    log.info('Node not found.');
}
// END


// BEGIN
// Fetches nodes.
var result2 = repo.get('nodeId', '/node2-path', 'node3Id');
log.info(result2.length + ' nodes found.');
// END

// BEGIN
// Node fetched.
var expected = {
    '_id': 'nodeId',
    '_name': 'my-name',
    '_path': '/my-name',
    '_childOrder': '_ts DESC',
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
                'path': 'displayName',
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
        ]
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
    '_state': 'DEFAULT',
    '_nodeType': 'default',
    '_versionKey': 'versionKey',
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
};
// END

assert.assertJsonEquals(expected, result1);
assert.assertJsonEquals(2, result2.length);
assert.assertJsonEquals(expected, result2[0]);
