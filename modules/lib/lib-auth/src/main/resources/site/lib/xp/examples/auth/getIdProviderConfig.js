var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
var result = authLib.getIdProviderConfig();
log.info('Field value for the current ID provider config = %s', result.Field);
// END

// BEGIN
// ID Provider config returned.
var expected = {
    "set": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue"
};
// END

assert.assertJsonEquals(expected, result);
