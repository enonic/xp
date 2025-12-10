var assert = require('/lib/xp/testing');

var TestClass = Java.type('com.enonic.xp.lib.node.BaseNodeHandlerTest');
var byteSource1 = TestClass.createByteSource('Hello World');
var byteSource2 = TestClass.createByteSource('Hello World2');

// BEGIN
var nodeLib = require('/lib/xp/node');

// Connect to repo
var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// Create node.
var result1 = repo.create({
    likes: 'plywood',
    numberOfUselessGadgets: 123
});

log.info('Node created with id ' + result1._id);
// END

// BEGIN
// Node created.
var expected = {
    '_id': 'a-random-node-id',
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
        'configs': [],
        'allTextLanguages': []
    },
    '_permissions': [
        {
            'principal': 'role:system.admin',
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
    'likes': 'plywood',
    'numberOfUselessGadgets': 123
};
// END

assert.assertJsonEquals(expected, result1);
