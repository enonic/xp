var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.createUser = function () {

    var result = auth.createUser({
        idProvider: 'myIdProvider',
        name: 'userId',
        displayName: 'user display name',
        email: 'user1@enonic.com'
    });

    var expectedJson = {
        'type': 'user',
        'key': 'user:enonic:user1',
        'displayName': 'User 1',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'disabled': false,
        'email': 'user1@enonic.com',
        'login': 'user1',
        'idProvider': 'enonic',
        'hasPassword': false
    };

    t.assertJsonEquals(expectedJson, result, 'createUser result not equals');

};

exports.createUserNoEmail = function () {

    var result = auth.createUser({
        idProvider: 'myIdProvider',
        name: 'userId',
        displayName: 'user display name'
    });

    var expectedJson = {
        'type': 'user',
        'key': 'user:enonic:user1',
        'displayName': 'User 1',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'disabled': false,
        'login': 'user1',
        'idProvider': 'enonic',
        'hasPassword': false
    };

    t.assertJsonEquals(expectedJson, result, 'createUser result not equals');

};

exports.createUserWithMissingArg = function () {

    auth.createUser({
        idProvider: 'myIdProvider',
        name: 'userId'
    });
};
