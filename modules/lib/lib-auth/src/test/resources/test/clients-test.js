var t = require('/lib/xp/testing.js');
var authLib = require('/lib/xp/auth.js');

exports.createClientDefaults = function () {
    var result = authLib.createClient({idProvider: 'enonic', id: 'cli'});

    t.assertJsonEquals({
        'idProvider': 'enonic',
        'id': 'cli',
        'displayName': 'cli',
        'grantTypes': [],
        'redirectUris': [],
        'scope': [],
        'resource': [],
        'enabled': true
    }, result);
};

exports.createClientDisabled = function () {
    var result = authLib.createClient({idProvider: 'enonic', id: 'cli', enabled: false});

    t.assertEquals(false, result.enabled);
};

exports.modifyClient = function () {
    var result = authLib.modifyClient({
        idProvider: 'system',
        id: 'claude',
        uri: null,
        scope: [],
        grantTtl: 'P1D'
    });

    t.assertTrue(result.uri === undefined, 'uri cleared');
    t.assertEquals(0, result.scope.length, 'limit lifted');
    t.assertEquals('PT24H', result.grantTtl);
    t.assertEquals('Claude', result.displayName, 'kept');
};

exports.modifyClientUnknown = function () {
    t.assertEquals(null, authLib.modifyClient({idProvider: 'system', id: 'nope', enabled: false}));
};

exports.getClientsEmpty = function () {
    t.assertEquals(0, authLib.getClients({idProvider: 'enonic'}).length);
};

exports.deleteClientUnknown = function () {
    t.assertEquals(false, authLib.deleteClient({idProvider: 'enonic', id: 'cli'}));
};

exports.requiredParameters = function () {
    var failed = false;
    try {
        authLib.createClient({idProvider: 'enonic'});
    } catch (e) {
        failed = e.message.indexOf('id') >= 0;
    }
    t.assertTrue(failed, 'id is required');
};
