var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets outbound dependencies of content by Content ID.
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
// Outbound dependencies as they are returned.
var expected = ['d898972d-f1eb-40a8-a7f2-16abd4c105da', '9efadb7b-bb14-4c74-82ec-cec95069d0c2'];
// END

assert.assertJsonEquals(expected, resultById);

// BEGIN
// Gets outbound dependencies of a content by Content Path.
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
