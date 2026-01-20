var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.getUserMemberships = function () {

    var result = auth.getMemberships('user:myIdProvider:userId');

    var expectedJson = [
        {
            'type': 'group',
            'key': 'group:system:group-a',
            'displayName': 'Group A',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'description': 'description'
        }
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getTransitiveUserMemberships = function () {

    var result = auth.getMemberships('user:myIdProvider:userId', true);

    var expectedJson = [
        {
            'type': 'group',
            'key': 'group:system:group-a',
            'displayName': 'Group A',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'description': 'description'
        }
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getUserMembershipsWithRoleAndGroup = function () {

    var result = auth.getMemberships('user:myIdProvider:userId');

    var expectedJson = [
        {
            'type': 'role',
            'key': 'role:aRole',
            'displayName': 'Role Display Name',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'description': 'description'
        },
        {
            'type': 'group',
            'key': 'group:system:group-a',
            'displayName': 'Group A',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'description': 'description'
        }
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getNonExistingMemberships = function () {

    var result = auth.getMemberships('user:myIdProvider:XXX');

    var expectedJson = [];

    t.assertJsonEquals(expectedJson, result);

};


exports.getMembershipsWithoutKey = function () {

    try {
        auth.getMemberships();
    } catch (e) {
        t.assertEquals("Parameter 'principalKey' is required", e.message);
    }
};
