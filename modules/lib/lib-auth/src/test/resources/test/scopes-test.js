var t = require('/lib/xp/testing.js');
var authLib = require('/lib/xp/auth.js');

exports.createScope = function () {
    var result = authLib.createScope({
        idProvider: 'enonic',
        id: 'profile',
        title: 'Profile',
        required: true
    });

    t.assertJsonEquals({
        'idProvider': 'enonic',
        'id': 'profile',
        'title': 'Profile',
        'roles': [],
        'required': true
    }, result);
};

exports.createScopeDefaults = function () {
    var result = authLib.createScope({idProvider: 'enonic', id: 'profile'});

    t.assertEquals('profile', result.title, 'the title falls back to the id');
    t.assertTrue(result.description === undefined, 'no description');
    t.assertEquals(0, result.roles.length);
    t.assertEquals(false, result.required);
};

exports.modifyScope = function () {
    var result = authLib.modifyScope({
        idProvider: 'system',
        id: 'content:write',
        title: null,
        roles: ['role:cms.cm.app']
    });

    t.assertJsonEquals({
        'idProvider': 'system',
        'id': 'content:write',
        'title': 'content:write',
        'description': 'Create, change and publish content in all projects',
        'roles': ['role:cms.cm.app'],
        'required': false
    }, result);
};

exports.modifyScopeUnknown = function () {
    var result = authLib.modifyScope({idProvider: 'system', id: 'nope', required: true});

    t.assertEquals(null, result);
};

exports.getScopesEmpty = function () {
    var result = authLib.getScopes({idProvider: 'enonic'});

    t.assertEquals(0, result.length);
};

exports.deleteScopeUnknown = function () {
    var result = authLib.deleteScope({idProvider: 'enonic', id: 'profile'});

    t.assertEquals(false, result);
};

exports.requiredParameters = function () {
    var failed = false;
    try {
        authLib.createScope({idProvider: 'enonic'});
    } catch (e) {
        failed = e.message.indexOf('id') >= 0;
    }
    t.assertTrue(failed, 'id is required');

    failed = false;
    try {
        authLib.getScopes({});
    } catch (e) {
        failed = e.message.indexOf('idProvider') >= 0;
    }
    t.assertTrue(failed, 'idProvider is required');
};
