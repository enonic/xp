var assert = require('/lib/xp/testing');
var context = require('/lib/xp/context');

exports.testNoChange = function () {
    var result = context.run({
        repository: 'com.enonic.cms.myproject',
        branch: 'draft',
    }, function () {
        return context.get();
    });

    assert.assertJsonEquals({
        'branch': 'draft',
        'repository': 'com.enonic.cms.myproject',
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

function customAttribute(name) {
    return context.get().attributes['custom.' + name];
}

exports.after = function () {
    // custom attributes live in the local scope shared by all dynamic tests of this run
    ['myObject', 'myScalar', 'myList', 'shared', 'detached'].forEach(function (name) {
        context.setCustomLocalAttribute(name, null);
    });
};

exports.testSetCustomLocalAttribute = function () {
    context.setCustomLocalAttribute('myObject', {
        a: 1,
        b: 'text',
        c: true,
        d: ['x', 2, false],
        e: {
            nested: ['deep']
        }
    });

    assert.assertJsonEquals({
        a: 1,
        b: 'text',
        c: true,
        d: ['x', 2, false],
        e: {
            nested: ['deep']
        }
    }, customAttribute('myObject'));

    context.setCustomLocalAttribute('myScalar', 42);
    assert.assertEquals(42, customAttribute('myScalar'));

    context.setCustomLocalAttribute('myList', ['a', 'b']);
    assert.assertJsonEquals(['a', 'b'], customAttribute('myList'));
};

exports.testSetCustomLocalAttribute_remove = function () {
    context.setCustomLocalAttribute('toRemoveWithNull', 'value');
    context.setCustomLocalAttribute('toRemoveWithNull', null);
    assert.assertTrue(customAttribute('toRemoveWithNull') === undefined);

    context.setCustomLocalAttribute('toRemoveWithUndefined', 'value');
    context.setCustomLocalAttribute('toRemoveWithUndefined');
    assert.assertTrue(customAttribute('toRemoveWithUndefined') === undefined);
};

exports.testSetCustomLocalAttribute_sharedWithNestedRun = function () {
    context.setCustomLocalAttribute('shared', 'outer');

    var inner = context.run({branch: 'draft'}, function () {
        var seen = customAttribute('shared');
        context.setCustomLocalAttribute('shared', 'inner');
        return seen;
    });

    assert.assertEquals('outer', inner);
    assert.assertEquals('inner', customAttribute('shared'));
};

exports.testSetCustomLocalAttribute_serializedOnWrite = function () {
    var source = {list: ['one']};
    context.setCustomLocalAttribute('detached', source);
    source.list.push('two');

    assert.assertJsonEquals({list: ['one']}, customAttribute('detached'));

    var read = customAttribute('detached');
    read.list.push('three');
    assert.assertJsonEquals({list: ['one']}, customAttribute('detached'));
};

function runExample(name) {
    testInstance.runScript('/lib/xp/examples/context/' + name + '.js');
}

exports.testExamples = function () {
    runExample('get');
    runExample('run');
};
