var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Find users with the specified email.
var result = authLib.findUsers({
    start: 0,
    count: 1,
    query: "email = 'user1@enonic.com'",
    sort: "modifiedTime DESC"
});
// END

// BEGIN
// Result for finding principals.
var expected = {
    "total": 1,
    "count": 1,
    "hits": [
        {
            "type": "user",
            "key": "user:enonic:user1",
            "displayName": "User 1",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "disabled": false,
            "email": "user1@enonic.com",
            "login": "user1",
            "userStore": "enonic",
            "profile": {
                "myApp": {
                    "subString": "subStringValue",
                    "subLong": 123
                },
                "string": "stringValue"
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
