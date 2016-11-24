var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/assert');

// BEGIN
var result = contentLib.getSite({
    key: '/path/to/mycontent'
});
log.info('Site name = %s', result._name);
// END

// BEGIN
// Site data returned.
var expected = {
    "_id": "100123",
    "_name": "my-content",
    "_path": "/my-content",
    "type": "base:unstructured",
    "hasChildren": false,
    "valid": false,
    "data": {
        "siteConfig": {
            "applicationKey": "myapplication",
            "config": {
                "Field": 42
            }
        }
    },
    "x": {},
    "page": {},
    "attachments": {},
    "publish": {}
};
// END

assert.assertJsonEquals(expected, result);
