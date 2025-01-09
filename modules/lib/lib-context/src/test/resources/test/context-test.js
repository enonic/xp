var assert = require('/lib/xp/testing');
var context = require('/lib/xp/context');

exports.testNoChange = function () {
    var result = context.run({}, function () {
        return context.get();
    });

    assert.assertJsonEquals({
        'branch': 'draft',
        'repository': 'com.enonic.cms.default',
        'authInfo': {
            'principals': [
                'user:system:anonymous',
                'role:system.everyone'
            ]
        },
        'attributes': {}
    }, result);
};

exports.testChange = function () {
    var result = context.run({
        repository: 'myrepository',
        branch: 'mybranch',
        user: {
            login: 'su',
            idProvider: 'system'
        },
        principals: ['role:system.myrole'],
        attributes: {
            'attr': 'value'
        }
    }, function () {
        return context.get();
    });

    assert.assertJsonEquals({
        'branch': 'mybranch',
        'repository': 'myrepository',
        'authInfo': {
            'user': {
                'type': 'user',
                'key': 'user:system:su',
                'displayName': 'Super User',
                'disabled': false,
                'login': 'su',
                'idProvider': 'system',
                'hasPassword': false
            },
            'principals': [
                'role:system.admin',
                'role:system.everyone',
                'user:system:su',
                'role:system.myrole'
            ]
        },
        'attributes': {
            'attr': 'value'
        }
    }, result);
};

function runExample(name) {
    testInstance.runScript('/lib/xp/examples/context/' + name + '.js');
}

exports.testExamples = function () {
    runExample('get');
    runExample('run');
};
