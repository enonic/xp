var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets a content by ID.
var resultById = contentLib.getOutboundDependencies({
    key: 'contentId'
});

if (resultById) {
    log.info('Outbound dependencies = ' + resultById);
} else {
    log.info('Outbound dependencies were not found');
}
// END

// BEGIN
// Content as it is returned.
var expected = {"contentIds": ["d898972d-f1eb-40a8-a7f2-16abd4c105da", "9efadb7b-bb14-4c74-82ec-cec95069d0c2"]};
// END

assert.assertJsonEquals(expected, resultById);

// BEGIN
// Gets a content by Path.
var resultByPath = contentLib.getOutboundDependencies({
    key: '/path/to/content'
});

if (resultByPath) {
    log.info('Outbound dependencies = ' + resultByPath);
} else {
    log.info('Outbound dependencies were not found');
}
// END

assert.assertJsonEquals(expected, resultByPath);
