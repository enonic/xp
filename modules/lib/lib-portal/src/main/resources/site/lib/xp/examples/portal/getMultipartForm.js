var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getMultipartForm();
log.info('Multipart form %s', result);
// END

// BEGIN
// Multipart-form returned.
var expected = {
    "item1": {
        "name": "item1",
        "fileName": "item1.jpg",
        "contentType": "image/png",
        "size": 10
    },
    "item2": [
        {
            "name": "item2",
            "fileName": "image1.png",
            "contentType": "image/png",
            "size": 123
        },
        {
            "name": "item2",
            "fileName": "image2.jpg",
            "contentType": "image/jpeg",
            "size": 456
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
