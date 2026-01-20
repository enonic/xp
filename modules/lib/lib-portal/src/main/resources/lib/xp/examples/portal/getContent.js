var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getContent();
log.info('Current content path = %s', result._path);
// END

// BEGIN
// Content returned.
var expected = {
    '_id': '123456',
    '_name': 'mycontent',
    '_path': '/a/b/mycontent',
    'creator': 'user:system:admin',
    'modifier': 'user:system:admin',
    'createdTime': '1970-01-01T00:00:00Z',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'type': 'base:unstructured',
    'displayName': 'My Content',
    'language': 'en',
    'valid': false,
    'data': {
        'a': '1'
    },
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};
// END

assert.assertJsonEquals(expected, result);
