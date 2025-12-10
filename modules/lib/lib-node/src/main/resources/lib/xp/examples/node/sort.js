var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Sort node
var result = repo.sort({
    key: 'nodeId',
    childOrder: 'field DESC'
});
// END

// BEGIN
// Node sorted
var expected = {
    node : {
        '_id': 'nodeId',
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
            ],
            'allTextLanguages': []
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
        },
        reorderedNodes: []
    }

};
// END

assert.assertNotNull(expected, result);

