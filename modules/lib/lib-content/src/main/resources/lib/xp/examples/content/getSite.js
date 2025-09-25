var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Get Site in path
var result = contentLib.getSite({
    key: '/path/to/mycontent'
});
log.info('Site name = %s', result._name);
// END

// BEGIN
// Get Site for Content id
var resultById = contentLib.getSite({
    key: '100124'
});
log.info('Site name = %s', result._name);
// END


// BEGIN
// Site data returned.
var expected = {
    '_id': '100123',
    '_name': 'my-content',
    '_path': '/my-content',
    'type': 'portal:site',
    'valid': false,
    'data': {
        'siteConfig': {
            'applicationKey': 'myapplication',
            'config': {
                'Field': 42
            }
        }
    },
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY',
        'checks': {}
    }
};
// END

assert.assertJsonEquals(expected, result);
assert.assertJsonEquals(expected, resultById);
