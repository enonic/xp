var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.createIdProvider = function () {

    var result = auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test',
        description: 'Id Provider used for testing',
        permissions: [
            {
                principal: 'role:system.admin',
                access: 'ADMINISTRATOR'
            }
        ]
    });

    var expectedJson = {
        'key': 'idProviderTestKey',
        'displayName': 'Id Provider test',
        'description': 'Id Provider used for testing'
    };

    t.assertJsonEquals(expectedJson, result, 'createIdProvider result not equals');
};

exports.createIdProviderMinimal = function () {

    var result = auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test'
    });

    var expectedJson = {
        'key': 'idProviderTestKey',
        'displayName': 'Id Provider test',
        'description': 'Id Provider used for testing'
    };

    t.assertJsonEquals(expectedJson, result, 'createIdProvider result not equals');
};

exports.createIdProviderMissingKey = function () {

    auth.createIdProvider({
        displayName: 'Id Provider test'
    });
};

exports.createIdProviderMissingDisplayName = function () {

    auth.createIdProvider({
        key: 'idProviderTestKey'
    });
};
