var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// patch nodes in several branches
var result = repo.patch({
    key: 'a',
    branches: ['master', 'draft'],
    editor: (node) => {
        node._childOrder = 'updatedOrderField DESC';

        node.myString = 'modified';
        node.myArray = ['modified1', 'modified2', 'modified3'];

        return node;
    }
});
// END

// BEGIN
// Node created.
var expected = {
    'branchResults': [
        {
            'branch': 'master',
            'node': {
                '_id': 'a',
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
                '_permissions': [
                    {
                        'principal': 'role:system.everyone',
                        'allow': [
                            'READ'
                        ],
                        'deny': []
                    }
                ],
                '_nodeType': 'default',
                'notChanged': 'originalValue',
                'myString': 'originalValue',
                'toBeRemoved': 'removeThis',
                'mySet': {
                    'myGeoPoint': '30.0,-30.0'
                }
            }
        },
        {
            'branch': 'draft',
            'node': {
                '_id': 'a',
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
                '_permissions': [
                    {
                        'principal': 'role:system.everyone',
                        'allow': [
                            'READ'
                        ],
                        'deny': []
                    }
                ],
                '_nodeType': 'default',
                'notChanged': 'originalValue',
                'myString': 'originalValue',
                'toBeRemoved': 'removeThis',
                'mySet': {
                    'myGeoPoint': '30.0,-30.0'
                }
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);


