var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getMultipartItem('item1');
log.info('Multipart item %s', result);
// END

// BEGIN
// Multipart-form returned.
var expected = {
    "name": "item1",
    "fileName": "item1.jpg",
    "contentType": "image/png",
    "size": 10
};
// END

assert.assertJsonEquals(expected, result);
