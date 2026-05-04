var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.getIdProviders = function () {

    var result = auth.getIdProviders();

    var expectedJson = [
        {
            'key': 'idProviderTestKey',
            'displayName': 'Id Provider test',
            'description': 'Id Provider used for testing'
        }
    ];

    t.assertJsonEquals(expectedJson, result, 'getIdProviders result not equals');
};

exports.getIdProvidersEmpty = function () {

    var result = auth.getIdProviders();

    t.assertJsonEquals([], result, 'getIdProviders result not equals');
};
