var assert = require('/lib/xp/assert.js');
var context = require('/lib/xp/context.js');

exports.noChange = function () {
    var result = context.run({}, function () {
        return context.get();
    });

    assert.assertJsonEquals({
        "branch": "draft",
        "repository": "cms-repo",
        "authInfo": {
            "principals": [
                "user:system:anonymous",
                "role:system.everyone"
            ]
        }
    }, result);
};

exports.change = function () {
    var result = context.run({
        branch: 'mybranch',
        user: 'su'
    }, function () {
        return context.get();
    });

    assert.assertJsonEquals({
        "branch": "mybranch",
        "repository": "cms-repo",
        "authInfo": {
            "user": {
                "type": "user",
                "key": "user:system:su",
                "displayName": "Super User",
                "disabled": false,
                "login": "su",
                "userStore": "system"
            },
            "principals": [
                "role:system.admin",
                "role:system.everyone",
                "user:system:su"
            ]
        }
    }, result);
};
