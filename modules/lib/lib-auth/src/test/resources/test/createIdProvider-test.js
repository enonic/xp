var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

var expectedFixtureResult = {
    'key': 'idProviderTestKey',
    'displayName': 'Id Provider test',
    'description': 'Id Provider used for testing',
    'idProviderConfig': {
        'applicationKey': 'com.enonic.app.test',
        'config': {
            'set': {
                'subString': 'subStringValue',
                'subLong': 123
            },
            'string': 'stringValue'
        }
    }
};

exports.createIdProvider = function () {

    var result = auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test',
        description: 'Id Provider used for testing',
        idProviderConfig: {
            applicationKey: 'com.enonic.app.test',
            config: {
                loginUrl: 'https://example.com/login',
                nested: {
                    flag: true,
                    count: 7
                }
            }
        },
        permissions: [
            {
                principal: 'role:system.admin',
                access: 'ADMINISTRATOR'
            }
        ]
    });

    t.assertJsonEquals(expectedFixtureResult, result, 'createIdProvider result not equals');
};

exports.createIdProviderMinimal = function () {

    var result = auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test'
    });

    t.assertJsonEquals(expectedFixtureResult, result, 'createIdProvider result not equals');
};

exports.createIdProviderConfigWithoutConfig = function () {

    auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test',
        idProviderConfig: {
            applicationKey: 'com.enonic.app.test'
        }
    });
};

exports.createIdProviderMissingApplicationKey = function () {

    auth.createIdProvider({
        key: 'idProviderTestKey',
        displayName: 'Id Provider test',
        idProviderConfig: {
            config: {
                anything: 'goes'
            }
        }
    });
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
