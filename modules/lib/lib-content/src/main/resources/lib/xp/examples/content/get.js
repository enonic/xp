var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets a single content by path.
var result = contentLib.get({
    key: '/path/to/mycontent'
});

if (result) {
    log.info('Display Name = ' + result.displayName);
} else {
    log.info('Content was not found');
}
// END

// BEGIN
// Gets a single content by id and versionId.
var resultByIdAndVersionId = contentLib.get({
    key: '123456',
    versionId: 'versionId'
});

if (resultByIdAndVersionId) {
    log.info('Display Name = ' + result.displayName);
} else {
    log.info('Content was not found');
}
// END

// BEGIN
// Content as it is returned.
var expected = {
    '_id': '123456',
    '_name': 'mycontent',
    '_path': '/path/to/mycontent',
    'creator': 'user:system:admin',
    'modifier': 'user:system:admin',
    'createdTime': '1970-01-01T00:00:00Z',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'type': 'base:unstructured',
    'displayName': 'My Content',
    'language': 'en',
    'valid': true,
    'childOrder': '_ts DESC, _name ASC',
    'data': {
        'myfield': 'Hello World'
    },
    'x': {},
    'page': {},
    'attachments': {
        'logo.png': {
            'name': 'logo.png',
            'label': 'small',
            'size': 6789,
            'mimeType': 'image/png'
        },
        'document.pdf': {
            'name': 'document.pdf',
            'size': 12345,
            'mimeType': 'application/pdf'
        }
    },
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};
// END

assert.assertJsonEquals(expected, result);
assert.assertJsonEquals(expected, resultByIdAndVersionId);
